# Backend temporário com H2 em memória. Não conecta ao MySQL.
$ErrorActionPreference = 'Stop'
$besRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$besClasspath = Join-Path $env:TEMP 'bes-frontend-h2-classpath.txt'
Push-Location $besRoot
try {
    & .\mvnw.cmd test-compile dependency:build-classpath '-DincludeScope=test' "-Dmdep.outputFile=$besClasspath"
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível preparar o backend isolado.' }
    $besDependencies = (Get-Content -Raw -LiteralPath $besClasspath).Trim()
    $besJavaClasspath = "target/test-classes;target/classes;$besDependencies"
    & java -cp $besJavaClasspath br.com.almoxarifado.ControleAlmoxarifadoApplication '--spring.profiles.active=test' '--spring.datasource.url=jdbc:h2:mem:bes-frontend;MODE=MySQL;DB_CLOSE_DELAY=-1' '--spring.datasource.driver-class-name=org.h2.Driver' '--spring.datasource.username=sa' '--spring.datasource.password=' '--spring.jpa.hibernate.ddl-auto=create-drop' '--spring.jpa.show-sql=false' '--spring.jpa.open-in-view=true' '--logging.level.org.hibernate.SQL=WARN' '--server.port=8081'
} finally { Pop-Location }
