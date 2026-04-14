# Paxl: Block Place Calm - Implementation Plan

## 1. Project Overview
Paxl is a relaxing yet strategic block puzzle game where players place various block shapes into an 8x8 grid to clear lines. The game features a vibrant retro-modern aesthetic with neon colors and "Press Start 2P" typography.

## 2. Architecture: MVVM + Jetpack Compose
- **Model**: Data classes for Blocks, Grid, Scores, and Coin Economy. Room database for persisting high scores and coin balance.
- **View**: Jetpack Compose for all screens and UI components.
- **ViewModel**: State management for gameplay logic, shop interactions, and navigation.

## 3. Screen Breakdown
1.  **Home**: Main entry point with 'Play', 'Shop', 'Settings', and 'Help' buttons.
2.  **Game Play**: The core 8x8 grid gameplay, score display, and power-up buttons (Undo, Reshuffle).
3.  **Mode Select**: Choice between Classic and potentially other modes (if added).
4.  **Coin Shop**: IAP integration for purchasing coin packs and viewing balance.
5.  **Settings**: Sound/Music toggles, Theme selection.
6.  **Help**: Instructions on how to play.
7.  **Pause**: Overlay during gameplay to resume or quit.
8.  **Result**: Game over screen showing final score, coins earned, and options to replay or go home.

## 4. Test-Driven Development (TDD) Strategy

### Scope
- **Unit Tests**: Gameplay logic (line clearing, block placement validation, score calculation), Coin Economy (deductions, additions), ViewModel state transitions.
- **Instrumented Tests**: Navigation flow, persistence (Room), and critical UI interactions.

### Test Cases (Examples)
- **Grid Logic**: 
    - Placing a block on an occupied cell should return failure.
    - Clearing a full row should increase score and clear cells.
    - Clearing multiple lines (combo) should award bonus points.
- **Coin Economy**:
    - Deducting coins for 'Undo' should fail if balance is insufficient.
    - Adding coins from 'Result' screen should update balance correctly.
- **Game Over**:
    - Game should end when no more blocks can be placed.

### Workflow
1.  Define the interface for a feature (e.g., `GridManager`).
2.  Write a failing unit test for a specific behavior.
3.  Implement the minimum code to make the test pass.
4.  Refactor and repeat.

## 5. Development Phases

### Phase 1: Setup & Theme (Current)
- Material 3 Theme with vibrant neon colors.
- "Press Start 2P" font integration.
- Edge-to-Edge support.
- Implementation Plan (TDD focus).

### Phase 2: Core Gameplay Logic (TDD)
- Grid management logic.
- Block generation and placement rules.
- Line clearing and scoring.

### Phase 3: UI Implementation
- Compose screens for Home, Play, and Result.
- Custom components for the 8x8 grid and block shapes.

### Phase 4: Coin Economy & Power-ups
- Persistence with Room.
- Undo, Reshuffle, and Continue logic.

### Phase 5: Shop & IAP
- Google Play Billing integration.
- Coin pack items and purchase flow.

### Phase 6: Polish & Audio
- SFX and background music.
- Animations and transitions.
- Final testing and bug fixes.

## 6. Technical Requirements Reference
- **Min SDK**: 24
- **Target SDK**: 36
- **Theme**: Material 3 (Expressive)
- **Typography**: Press Start 2P
- **Display**: Full Edge-to-Edge
