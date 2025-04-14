@echo off
setlocal enabledelayedexpansion

:: 配置部分
set "root_dir=D:\workspaces\java\projects\rade"  :: 替换为您的Git项目根目录
set "default_delay=300"                     :: 默认等待时间(秒)，当文件中没有指定时使用

:: 获取所有序号文件并排序
set /a file_count=0
for %%f in ("%root_dir%\*.txt") do (
    for /f "delims=. tokens=1" %%n in ("%%~nf") do (
        set "file_order[%%n]=%%f"
        set /a file_count+=1
    )
)

:: 检查是否找到序号文件
if %file_count% equ 0 (
    echo 错误: 没有找到任何序号文件(1.txt, 2.txt等)
    pause
    exit /b 1
)

:: 显示执行顺序
echo 将按以下顺序处理项目:
for /f "tokens=2,3 delims=[]=" %%a in ('set file_order[') do (
    echo 顺序:%%a - 文件:%%b
)

:: 处理每个项目
for /f "tokens=2,3 delims=[]=" %%a in ('set file_order[') do (
    set "order=%%a"
    set "file_path=%%b"
    set "project_dir=%~dp0%%~nb"
    set "project_dir=!project_dir:~0,-2!"  :: 去掉.txt部分
    
    :: 从文件中读取等待时间(第一行)
    set "delay_time="
    <"!file_path!" (
        set /p delay_time=
    )
    
    :: 如果没有指定时间，使用默认值
    if "!delay_time!"=="" (
        set "delay_time=%default_delay%"
    )
    
    echo.
    echo ==============================================
    echo 正在处理顺序 !order!: !project_dir!
    echo 等待时间: !delay_time! 秒
    echo ==============================================
    
    :: 检查项目目录是否存在
    if not exist "!project_dir!\" (
        echo 错误: 项目目录不存在 - !project_dir!
        timeout /t 5 >nul
        goto :next_project
    )
    
    cd /d "!project_dir!"
    
    :: 检查是否是Git仓库
    git rev-parse --is-inside-work-tree 2>nul
    if errorlevel 1 (
        echo 错误: !project_dir! 不是Git仓库
        timeout /t 5 >nul
        goto :next_project
    )
    
    :: 获取当前分支
    for /f "delims=" %%b in ('git rev-parse --abbrev-ref HEAD') do set "current_branch=%%b"
    echo 当前分支: !current_branch!
    
    :: 确定目标分支
    set "target_branch="
    if "!current_branch!"=="master" (
        echo 跳过master分支
        goto :next_project
    )
    
    if "!current_branch:~0,3!"=="sit" (
        set "target_branch=dev-!current_branch:~4!"
    ) else if "!current_branch:~0,3!"=="dev" (
        set "target_branch=sit-!current_branch:~4!"
    )
    
    if "!target_branch!"=="" (
        echo 无法识别的分支格式: !current_branch!
        goto :next_project
    )
    
    :: 检查目标分支是否存在
    git show-ref --verify --quiet refs/heads/!target_branch!
    if errorlevel 1 (
        echo 目标分支 !target_branch! 不存在
        goto :next_project
    )
    
    :: 执行操作
    echo 正在提交当前更改...
    git add .
    git commit -m "自动提交: 合并前的更改" || echo 没有需要提交的更改
    
    echo 正在切换到目标分支 !target_branch!...
    git checkout !target_branch!
    
    echo 正在合并 !current_branch! 到 !target_branch!...
    git merge !current_branch! --no-ff -m "自动合并: 从!current_branch!合并到!target_branch!"
    
    echo 正在推送更改...
    git push origin !target_branch!
    
    :: 切换回原始分支
    echo 正在切换回原始分支 !current_branch!...
    git checkout !current_branch!
    
    :next_project
    echo 操作完成，等待 !delay_time! 秒后继续...
    timeout /t !delay_time! >nul
)

echo.
echo ==============================================
echo 所有项目处理完成!
echo ==============================================
pause