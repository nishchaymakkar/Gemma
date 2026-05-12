$files = Get-ChildItem -Path "d:\downloads\Gemma\app\src\main\java\com\gemma\app\presentation\chat\components" -Recurse -Filter *.kt

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    $content = $content -replace 'Color\(0x29000000\)', 'MaterialTheme.colorScheme.outline'
    
    if ($content -ne $originalContent -and $content -match 'MaterialTheme\.colorScheme' -and $content -notmatch 'import androidx\.compose\.material3\.MaterialTheme') {
        $content = $content -replace 'import androidx\.compose\.runtime\.Composable', "import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable"
    }

    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content
    }
}
