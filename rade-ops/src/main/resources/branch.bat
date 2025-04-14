@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: 配置部分
set "root_dir=D:\workspaces\java\projects\rade"
set "default_delay=300"

:: [其他部分保持不变...]

:: 处理每个项目
for /f "tokens=2,3 delims=[]=" %%a in ('set file_order[') do (
    set "order=%%a"
    set "file_name=%%b"
    set "file_path=%root_dir%\!file_name!"

    :: 从文件名获取项目目录名(去掉.txt)
    set "project_dir=%root_dir%\!file_name:~0,-4!"

    :: 从文件中读取等待时间(第一行) - 修复的部分
    set "delay_time=%default_delay%"
    if exist "!file_path!" (
        for /f "usebackq delims=" %%t in ("!file_path!") do (
            set "delay_time=%%t"
            goto :delay_read
        )
    )
    :delay_read

    :: 验证等待时间是否为数字
    echo !delay_time! | findstr /r "^[0-9][0-9]*$" >nul
    if errorlevel 1 (
        set "delay_time=%default_delay%"
        echo Warning: Invalid delay time, using default !delay_time! seconds
    )

    echo.
    echo ==============================================
    echo [Order !order!] Processing: !project_dir!
    echo Wait time: !delay_time! seconds
    echo ==============================================

    :: 检查项目目录是否存在
    if not exist "!project_dir!\" (
        echo Error: Project directory not found - !project_dir!
        timeout /t 5 >nul
        goto :next_project
    )

    pushd "!project_dir!"

    :: 检查是否是Git仓库
    git rev-parse --is-inside-work-tree 2>nul
    if errorlevel 1 (
        echo Error: !project_dir! is not a Git repository
        popd
        timeout /t 5 >nul
        goto :next_project
    )

    :: 获取当前分支
    set "current_branch="
    for /f "delims=" %%b in ('git rev-parse --abbrev-ref HEAD 2^>nul') do set "current_branch=%%b"

    if "!current_branch!"=="" (
        echo Error: Cannot get current branch
        popd
        goto :next_project
    )

    echo Current branch: !current_branch!

    :: 跳过master/main分支
    if /i "!current_branch!"=="master" (
        echo Skipping master branch
        popd
        goto :next_project
    )
    if /i "!current_branch!"=="main" (
        echo Skipping main branch
        popd
        goto :next_project
    )

    :: 确定目标分支
    set "target_branch="
    if "!current_branch:~0,3!"=="sit" (
        set "target_branch=dev-!current_branch:~4!"
    ) else if "!current_branch:~0,3!"=="dev" (
        set "target_branch=sit-!current_branch:~4!"
    )

    if "!target_branch!"=="" (
        echo Unrecognized branch format: !current_branch!
        echo Expected format: sit-1.0.xxxx or dev-1.0.xxxx
        popd
        goto :next_project
    )

    :: 检查目标分支是否存在
    git show-ref --verify --quiet refs/heads/!target_branch! 2>nul
    if errorlevel 1 (
        echo Target branch !target_branch! does not exist
        popd
        goto :next_project
    )

    :: 执行操作
    echo Committing current changes...
    git add . 2>nul
    git commit -m "Auto commit: changes before merge" 2>nul || echo Info: No changes to commit

    echo Switching to target branch !target_branch!...
    git checkout !target_branch! 2>nul
    if errorlevel 1 (
        echo Error: Cannot switch to branch !target_branch!
        popd
        goto :next_project
    )

    echo Merging !current_branch! into !target_branch!...
    git merge !current_branch! --no-ff -m "Auto merge: from !current_branch! to !target_branch!" 2>nul
    if errorlevel 1 (
        echo Error: Merge conflict detected, please resolve manually
        popd
        goto :next_project
    )

    echo Pushing changes...
    git push origin !target_branch! 2>nul
    if errorlevel 1 (
        echo Error: Push failed
    )

    :: 切换回原始分支
    echo Switching back to original branch !current_branch!...
    git checkout !current_branch! 2>nul

    popd

    :next_project
    echo Operation completed, waiting !delay_time! seconds...
    timeout /t !delay_time! >nul
)

echo.
echo ==============================================
echo All projects processed!
echo ==============================================
pause