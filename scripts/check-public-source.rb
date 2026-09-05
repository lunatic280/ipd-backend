#!/usr/bin/env ruby
# 전시용 소스의 공개 금지 파일과 설정만 검사합니다. 앱 빌드/실행이나 취약점 인증이 아닙니다.
require 'open3'
require 'rexml/document'

root = File.expand_path('..', __dir__)
def git(root, *args, input: '')
  out, err, status = Open3.capture3('git', '-C', root, *args, stdin_data: input)
  abort "Git check failed: #{args.first}" unless status.success?
  out
end
def check(condition, message)
  abort "FAIL: #{message}" unless condition
end

files = git(root, 'ls-files', '-z', '--cached', '--others', '--exclude-standard').split("\0").uniq
forbidden = %r{(?:\A|/)(?:\.env(?:\..+)?|\.idea|\.cursor|\.aws|\.supabase|\.github/workflows|logs|uploads|dumps|backups|secrets|signing|local\.properties|google-services\.json)(?:/|\z)|\.(?:pem|key|p12|pfx|jks|keystore|crt|cer|der|log|sql|dump|bak|db|sqlite\w*|apk|aab|hprof|zip|gz)\z}i
files.each do |relative|
  next if relative == '.env.example'
  check(!relative.match?(forbidden), "excluded file is publishable: #{relative}")
end
texts = {}
files.each do |relative|
  path = File.join(root, relative)
  check(!File.symlink?(path), "symlink: #{relative}")
  body = File.binread(path).force_encoding('UTF-8')
  if !body.valid_encoding? || body.include?("\0")
    check(relative == 'gradle/wrapper/gradle-wrapper.jar', "unexpected binary: #{relative}")
    next
  end
  texts[relative] = body
  next if relative == 'scripts/check-public-source.rb'
  check(!body.match?(/-----BEGIN [A-Z ]*PRIVATE KEY-----|\b(?:AKIA|ASIA)[A-Z0-9]{16}\b|\bgh[pousr]_[A-Za-z0-9]{20,}\b/), "credential marker: #{relative}")
  check(!body.match?(%r{/Users/|/home/|\b(?:10|192\.168|172\.(?:1[6-9]|2\d|3[01]))\.\d+\.\d+(?:\.\d+)?\b}), "private address/path: #{relative}")
  body.scan(/[A-Za-z0-9._%+-]+@([A-Za-z0-9.-]+\.[A-Za-z]{2,})/).flatten.each do |domain|
    check(['example.invalid', 'example.com', 'users.noreply.github.com'].include?(domain), "non-example email: #{relative}")
  end
  if relative.end_with?('.kt')
    check(!body.match?(/\bLog\.[vdiew]\s*\(|printStackTrace\s*\(|println\s*\(/), "verbose output: #{relative}")
    body.lines.grep(/\b(?:logger|log)\.(?:info|warn|error|debug|trace)\(/).each do |line|
      check(line.strip.match?(/\Alogger\.error\("(?:MQTT connection failed|Async task failed)"\)\z/), "dynamic logging: #{relative}")
    end
  end
  if relative.end_with?('.xml')
    REXML::Document.new(body)
  end
end

example = texts.fetch('.env.example')
example.lines.reject { |line| line.start_with?('#') || line.strip.empty? }.each do |line|
  key, value = line.strip.split('=', 2)
  check(value != nil, 'malformed env example')
  if key.match?(/PASSWORD|SECRET|TOKEN|CLIENT_ID|DB_USERNAME/)
    check(value.empty?, "nonempty example credential: #{key}")
  end
end
ignored = ['.env', '.env.production', 'local.properties', 'private.pem', 'cloud-credentials.json',
           'google-services.json', 'uploads/example.png', 'logs/example.log', 'dumps/example.sql']
ignored.each do |path|
  _, _, status = Open3.capture3('git', '-C', root, 'check-ignore', '--no-index', '--quiet', path)
  check(status.success?, "missing ignore rule: #{path}")
end
_, _, example_status = Open3.capture3('git', '-C', root, 'check-ignore', '--no-index', '--quiet', '.env.example')
check(!example_status.success?, '.env.example must remain visible')
check(texts.fetch('README.md').include?('전시용 소스'), 'missing showcase notice')

if texts.key?('src/main/resources/application.yml')
  config = texts.fetch('src/main/resources/application.yml')
  check(config.include?('show-sql: false') && config.include?('ddl-auto: none'), 'unsafe DB defaults')
  check(config.include?('enabled: ${MQTT_ENABLED:false}'), 'MQTT must default to disabled')
  user = texts.fetch('src/main/kotlin/com/example/Ipd/user/service/UserService.kt')
  check(user.include?('role = UserRole.USER') && !user.include?('request.role'), 'client-controlled signup role')
  security = texts.fetch('src/main/kotlin/com/example/Ipd/config/SecurityConfig.kt')
  check(security.include?('requestMatchers("/qr/**", "/led/**", "/test").denyAll()'), 'device routes must be disabled')
  check(security.include?('requestMatchers("/directions", "/get").authenticated()'), 'map proxy must require auth')
  env_names = example.lines.map { |line| line[/\A([A-Z][A-Z0-9_]*)=/, 1] }.compact
  config.scan(/\$\{([A-Z][A-Z0-9_]*)(?::[^}]*)?\}/).flatten.each do |name|
    check(env_names.include?(name), "undocumented environment variable: #{name}")
  end
else
  build = texts.fetch('app/build.gradle')
  check(build.include?('.orElse("https://example.invalid/")'), 'real default API URL')
  check(build.include?('.orElse("YOUR_NAVER_MAP_CLIENT_ID")'), 'real default map client ID')
  manifest = texts.fetch('app/src/main/AndroidManifest.xml')
  check(manifest.include?('android:usesCleartextTraffic="false"'), 'HTTP must be disabled')
  check(manifest.include?('android:allowBackup="false"'), 'backup must be disabled')
  token = texts.fetch('app/src/main/java/com/example/ipdapplication/auth/TokenStore.kt')
  check(!token.include?('SharedPreferences') && token.include?('sessionToken = null'), 'token must be memory-only')
  signup = texts.fetch('app/src/main/java/com/example/ipdapplication/auth/dto/AuthDto.kt')
  check(!signup.match?(/data class SignRequest\([^)]*val role[^)]*\)/), 'signup must not submit a role')
end

puts "PASS: #{File.basename(root)} — #{files.length} files; publication checks and XML parsing passed (no build/run)."
