# b1290_pre

## Repo简介

这是一个使用 Java 开发的 BlackJack（21点）游戏项目，采用 AWT 和 Swing 图形库构建图形用户界面。

**项目名称**：BlackJack Game (Java)

**主要功能**：
- 完整的 BlackJack 游戏逻辑实现
- 图形化用户界面（使用 AWT/Swing）
- 卡牌动画和游戏状态管理
- 投注系统，玩家可以下注并管理余额
- 游戏结果显示（得分、输赢状态）

**技术栈**：
- Java
- AWT (Abstract Window Toolkit)
- Swing (GUI 框架)

**项目结构**：
- `App.java` - 应用程序入口点
- `BlackJack.java` - 游戏核心逻辑，包括发牌、计算得分、游戏状态管理
- `GameFrame.java` - 主窗口框架，管理 UI 组件和事件处理
- `GamePanel.java` - 游戏面板，负责卡牌渲染和动画效果
- `Card.java` - 卡牌数据模型
- `cards/` - 卡牌图片资源目录
- `Dockerfile` - Docker 容器化配置
- `build_docker.sh` / `run_docker.sh` - Docker 构建和运行脚本

**核心特性**：
- 支持玩家和庄家对局
- 自动处理 A 牌的特殊计分规则（1点或11点）
- 游戏动画效果（卡牌旋转、显示动画）
- 投注界面和余额管理
- 游戏结果显示（得分、输赢文本）

## 题目Prompt

Can you remove excessive spacing from the betting overlay and move it down to make the score and win text from the previous game remain visible above it? Feel free to remove the "Place Your Bet" text after the initial round if it doesn't all fit neatly beneath the score.

## PR链接

待创建
