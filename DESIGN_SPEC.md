# Summarize AI - Android Design Specification
## For Jetpack Compose Implementation

---

## 📱 App Overview

**App Name:** Summarize AI  
**Tagline:** "Summarize Smarter"  
**Platform:** Android (Jetpack Compose)  
**Design Style:** Modern, minimal, clean with light neutral backgrounds, rounded corners, soft shadows, and teal/blue accent colors

---

## 🎨 Design System

### Color Palette

```kotlin
// Primary Colors
val Cyan600 = Color(0xFF0891B2)      // Primary accent
val Blue600 = Color(0xFF2563EB)      // Secondary accent
val Cyan700 = Color(0xFF0E7490)      // Hover states
val Blue700 = Color(0xFF1D4ED8)      // Hover states

// Neutral Colors
val Gray50 = Color(0xFFF9FAFB)       // Background
val Gray100 = Color(0xFFF3F4F6)      // Muted backgrounds
val Gray200 = Color(0xFFE5E7EB)      // Borders
val Gray300 = Color(0xFFD1D5DB)      // Disabled states
val Gray400 = Color(0xFF9CA3AF)      // Icons (inactive)
val Gray500 = Color(0xFF6B7280)      // Secondary text
val Gray600 = Color(0xFF4B5563)      // Icons (active)
val Gray700 = Color(0xFF374151)      // Body text
val Gray800 = Color(0xFF1F2937)      // Emphasis text
val Gray900 = Color(0xFF111827)      // Headings
val White = Color(0xFFFFFFFF)        // Cards, inputs

// Accent Colors (for variety)
val Cyan50 = Color(0xFFECFEFF)       // Light backgrounds
val Blue50 = Color(0xFFEFF6FF)       // Light backgrounds
val Purple50 = Color(0xFFFAF5FF)     // Settings icon bg
val Purple600 = Color(0xFF9333EA)    // Settings icon

// Semantic Colors
val Green600 = Color(0xFF16A34A)     // Success states
val Red50 = Color(0xFFFEF2F2)        // Error backgrounds
val Red600 = Color(0xFFDC2626)       // Error states
```

### Typography

```kotlin
// Font Family
val fontFamily = FontFamily.Default  // System font (Roboto on Android)

// Text Styles
val Heading1 = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 36.sp,
    color = Gray900
)

val Heading2 = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 28.sp,
    color = Gray900
)

val Heading3 = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 24.sp,
    color = Gray900
)

val BodyLarge = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    color = Gray700
)

val BodyMedium = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.sp,
    color = Gray600
)

val BodySmall = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp,
    color = Gray500
)

val ButtonText = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 24.sp,
    color = White
)

val TabLabel = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 16.sp
)
```

### Spacing System

```kotlin
val spacing = object {
    val xs = 4.dp      // 4dp
    val sm = 8.dp      // 8dp
    val md = 12.dp     // 12dp
    val lg = 16.dp     // 16dp
    val xl = 24.dp     // 24dp
    val xxl = 32.dp    // 32dp
    val xxxl = 48.dp   // 48dp
}
```

### Border Radius

```kotlin
val cornerRadius = object {
    val sm = 8.dp      // Small elements
    val md = 12.dp     // Medium elements
    val lg = 16.dp     // Cards
    val xl = 20.dp     // Large cards
    val xxl = 24.dp    // Buttons, major elements
    val full = 9999.dp // Circular/pill shapes
}
```

### Elevation/Shadows

```kotlin
val elevation = object {
    val none = 0.dp
    val sm = 2.dp      // Subtle shadow
    val md = 4.dp      // Card shadow
    val lg = 8.dp      // Elevated elements
    val xl = 16.dp     // Modal/dialog shadow
}

// Custom shadow colors
val shadowColor = Color(0x0F000000)  // Black with 6% opacity
val accentShadow = Color(0x4D06B6D4) // Cyan shadow for primary buttons (30% opacity)
```

---

## 📐 Layout Specifications

### Screen Dimensions
- **Design Width:** 390dp (standard mobile)
- **Design Height:** 844dp (reference)
- **Status Bar:** System default
- **Bottom Navigation:** 64dp height

### Safe Areas
- **Top padding:** 16dp (below status bar)
- **Bottom padding:** 16dp (above navigation)
- **Side padding:** 24dp (default horizontal margins)

---

## 📄 Screen Specifications

### 1. Welcome/Onboarding Screen

**Purpose:** First screen users see when opening the app

**Layout:**
```
┌─────────────────────────┐
│                         │
│    [Gradient Circle]    │ <- Decorative background
│                         │
│   "Summarize Smarter"   │ <- Heading1, centered
│                         │
│   "Turn long text..."   │ <- BodyLarge, centered, gray-600
│                         │
│                         │
│   [AI Illustration]     │ <- Placeholder for AI/document graphic
│                         │
│                         │
│  [Get Started Button]   │ <- Primary gradient button
│  [Learn More Button]    │ <- Secondary outline button
│                         │
└─────────────────────────┘
```

**Components:**
- **Background:** White with decorative cyan-to-blue gradient circle (semi-transparent, large, positioned top-center)
- **Headline:** "Summarize Smarter"
  - Style: Heading1 (28sp, Bold)
  - Color: Gray-900
  - Alignment: Center
  - Margin top: 80dp
  
- **Subtitle:** "Turn long text into key insights instantly"
  - Style: BodyLarge (16sp)
  - Color: Gray-600
  - Alignment: Center
  - Margin top: 12dp
  - Max width: 300dp

- **Illustration Placeholder:** 
  - Size: 280dp x 280dp
  - Centered
  - Margin top: 48dp
  - Background: Light gray circle
  - Note: Replace with AI/document illustration

- **Get Started Button:**
  - Width: Match parent (with 24dp side margins)
  - Height: 56dp
  - Background: Linear gradient (Cyan-600 to Blue-600)
  - Corner radius: 24dp
  - Text: "Get Started"
  - Shadow: 8dp elevation with cyan tint
  - Margin bottom: 12dp

- **Learn More Button:**
  - Width: Match parent (with 24dp side margins)
  - Height: 56dp
  - Background: Transparent
  - Border: 2dp solid Gray-200
  - Corner radius: 24dp
  - Text: "Learn More"
  - Text color: Gray-700
  - Margin bottom: 32dp

---

### 2. Home/Input Screen (Tab 1)

**Purpose:** Main screen where users input text to summarize

**Layout:**
```
┌─────────────────────────┐
│  Summarize AI           │ <- Top bar (white bg)
├─────────────────────────┤
│                         │
│  ┌───────────────────┐  │
│  │                   │  │
│  │  [Text Input]     │  │ <- Large textarea
│  │  "Paste or..."    │  │
│  │                   │  │
│  └───────────────────┘  │
│                         │
│  ┌───────────────────┐  │
│  │ 📤 Upload PDF... │  │ <- Upload button (dashed border)
│  └───────────────────┘  │
│                         │
│  ┌───────────────────┐  │
│  │   Summarize       │  │ <- Primary gradient button
│  └───────────────────┘  │
│                         │
├─────────────────────────┤
│  🏠  🕐  💾  ⚙️       │ <- Bottom navigation
└─────────────────────────┘
```

**Components:**

**Top App Bar:**
- Height: 64dp
- Background: White
- Border bottom: 1dp solid Gray-200
- Title: "Summarize AI"
  - Style: Heading2 (20sp, SemiBold)
  - Color: Gray-900
  - Padding: 24dp horizontal, 16dp vertical

**Text Input Area:**
- Width: Match parent (with 24dp side margins)
- Height: Flexible (flex-grow to fill available space)
- Background: White
- Border: 1dp solid Gray-200
- Corner radius: 16dp
- Placeholder: "Paste or upload your text here..."
- Placeholder color: Gray-400
- Text color: Gray-900
- Padding: 24dp
- Margin: 24dp top
- Shadow: 2dp elevation

**Upload Button:**
- Width: Match parent (with 24dp side margins)
- Height: 56dp
- Background: White
- Border: 2dp dashed Gray-300
- Corner radius: 16dp
- Icon: Upload icon (20dp)
- Text: "Upload PDF or DOC"
- Text color: Gray-700
- Margin: 16dp top
- Hover state: Border color changes to Cyan-400

**Summarize Button:**
- Width: Match parent (with 24dp side margins)
- Height: 56dp
- Background: Linear gradient (Cyan-600 to Blue-600)
- Corner radius: 24dp
- Text: "Summarize"
- Shadow: 8dp elevation with cyan tint
- Margin: 24dp top and bottom
- Disabled state: 50% opacity, no shadow

**Bottom Navigation Bar:**
- Height: 64dp
- Background: White
- Border top: 1dp solid Gray-200
- 4 tabs evenly distributed
- Each tab:
  - Icon: 24dp
  - Label: 12sp
  - Active state: Cyan-600 color, bolder icon
  - Inactive state: Gray-400 color
  - Padding: 8dp vertical

---

### 3. Loading/Processing Screen

**Purpose:** Show progress while AI processes the text

**Layout:**
```
┌─────────────────────────┐
│                         │
│                         │
│                         │
│    [Animated Dots]      │ <- Pulsing animation
│                         │
│   "Analyzing text..."   │ <- Status text
│                         │
│   [Progress Bar]        │ <- Linear progress indicator
│                         │
│   "Please wait while"   │ <- Helper text
│   "we summarize..."     │
│                         │
│                         │
└─────────────────────────┘
```

**Components:**

**Background:** Gray-50 (light background)

**Animated Indicator:**
- 3 dots in a row
- Size: 16dp each
- Color: Cyan-600
- Spacing: 12dp between dots
- Animation: Scale pulsing (0.8x to 1.2x), sequential delay
- Positioned: Center of screen

**Status Text:**
- Text: "Analyzing text..."
- Style: Heading3 (18sp, SemiBold)
- Color: Gray-900
- Alignment: Center
- Margin top: 24dp

**Progress Bar:**
- Width: 240dp
- Height: 4dp
- Background: Gray-200
- Fill color: Linear gradient (Cyan-600 to Blue-600)
- Corner radius: 4dp (full)
- Animation: Indeterminate progress
- Margin top: 24dp
- Centered horizontally

**Helper Text:**
- Text: "Please wait while we summarize your content"
- Style: BodyMedium (14sp)
- Color: Gray-600
- Alignment: Center
- Max width: 280dp
- Margin top: 16dp

---

### 4. Output/Summary Screen

**Purpose:** Display the generated summary with different length options

**Layout:**
```
┌─────────────────────────┐
│  ← Summary              │ <- Top bar with back button
├─────────────────────────┤
│  [Short][Medium][Detailed] <- Tab selector
│                         │
│  ┌───────────────────┐  │
│  │                   │  │
│  │  Summary text...  │  │ <- Summary card
│  │                   │  │
│  └───────────────────┘  │
│                         │
├─────────────────────────┤
│  [Copy] [Save]          │ <- Action buttons
│  [Share]                │
└─────────────────────────┘
```

**Components:**

**Top App Bar:**
- Height: 64dp
- Background: White
- Border bottom: 1dp solid Gray-200
- Back button (left):
  - Icon: Arrow left (24dp)
  - Color: Gray-600
  - Size: 40dp touch target
  - Corner radius: 12dp
  - Hover background: Gray-100
- Title: "Summary"
  - Style: Heading2 (20sp, SemiBold)
  - Color: Gray-900

**Tab Selector:**
- Background: White
- Corner radius: 16dp
- Padding: 4dp
- Shadow: 2dp elevation
- Margin: 24dp horizontal, 24dp top
- 3 tabs: "Short", "Medium", "Detailed"
- Each tab:
  - Height: 40dp
  - Flex: 1 (equal width)
  - Corner radius: 12dp
  - Active state:
    - Background: Gray-900
    - Text color: White
  - Inactive state:
    - Background: Transparent
    - Text color: Gray-600

**Summary Card:**
- Width: Match parent (with 24dp margins)
- Background: White
- Border: 1dp solid Gray-200
- Corner radius: 16dp
- Padding: 24dp
- Shadow: 2dp elevation
- Margin: 24dp top
- Text:
  - Style: BodyLarge (16sp)
  - Color: Gray-700
  - Line height: 24sp

**Action Buttons Container:**
- Background: White
- Border top: 1dp solid Gray-200
- Padding: 24dp

**Copy and Save Buttons (Row):**
- Layout: 2 columns with 12dp gap
- Each button:
  - Height: 56dp
  - Flex: 1 (equal width)
  - Background: White
  - Border: 2dp solid Gray-200
  - Corner radius: 16dp
  - Icon + text
  - Text color: Gray-900
  - Hover: Background Gray-50

**Save Button Active State:**
- Border: 2dp solid Cyan-600
- Background: Cyan-50
- Icon: Filled bookmark
- Icon color: Cyan-600

**Share Button:**
- Width: Match parent
- Height: 56dp
- Background: Linear gradient (Cyan-600 to Blue-600)
- Corner radius: 24dp
- Icon: Share icon (20dp)
- Text: "Share"
- Shadow: 8dp elevation with cyan tint
- Margin top: 12dp

---

### 5. History Screen (Tab 2)

**Purpose:** Show list of previously summarized texts

**Layout:**
```
┌─────────────────────────┐
│  History                │ <- Top bar
│  [Search box]           │ <- Search input
├─────────────────────────┤
│  ┌───────────────────┐  │
│  │ 📄 Summary text   │  │ <- History item card
│  │    1h ago     🗑️  │  │
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ 📄 Summary text   │  │
│  │    2h ago     🗑️  │  │
│  └───────────────────┘  │
│                         │
├─────────────────────────┤
│  🏠  🕐  💾  ⚙️       │ <- Bottom navigation
└─────────────────────────┘
```

**Components:**

**Top Bar:**
- Height: Auto
- Background: White
- Border bottom: 1dp solid Gray-200
- Padding: 24dp horizontal, 16dp vertical

**Title:**
- Text: "History"
- Style: Heading2 (20sp, SemiBold)
- Color: Gray-900
- Margin bottom: 16dp

**Search Input:**
- Width: Match parent
- Height: 48dp
- Background: Gray-50
- Border: 1dp solid Gray-200
- Corner radius: 12dp
- Icon: Search icon (20dp) on left
- Placeholder: "Search history..."
- Padding: 12dp horizontal
- Margin bottom: 16dp

**Content Area:**
- Background: Gray-50
- Padding: 24dp
- Scrollable: Vertical

**Empty State** (when no history):
- Icon: Clock icon (48dp)
- Icon background: Gray-100 circle (96dp)
- Icon color: Gray-400
- Title: "No History Yet"
  - Style: Heading3 (18sp, SemiBold)
  - Color: Gray-900
- Description: "Your summarized texts will appear here"
  - Style: BodyMedium (14sp)
  - Color: Gray-500
- Centered vertically and horizontally

**History Item Card:**
- Width: Match parent
- Background: White
- Border: 1dp solid Gray-200
- Corner radius: 16dp
- Padding: 16dp
- Shadow: 2dp elevation
- Margin bottom: 12dp

**History Item Layout:**
- Row layout with icon, content, delete button
- Icon container:
  - Size: 44dp x 44dp
  - Background: Cyan-50
  - Corner radius: 12dp
  - Icon: File text (20dp)
  - Icon color: Cyan-600
- Content area:
  - Flex: 1
  - Padding: 0 12dp
  - Summary text:
    - Style: BodyMedium (14sp)
    - Color: Gray-800
    - Max lines: 2
    - Ellipsis: End
  - Timestamp:
    - Style: BodySmall (12sp)
    - Color: Gray-500
    - Margin top: 8dp
- Delete button:
  - Size: 32dp x 32dp
  - Icon: Trash (16dp)
  - Color: Gray-400
  - Hover: Red-50 background, Red-600 color
  - Corner radius: 8dp

---

### 6. Saved Screen (Tab 3)

**Purpose:** Show bookmarked/saved summaries

**Layout:**
```
┌─────────────────────────┐
│  Saved                  │ <- Top bar
│  [Search box]           │ <- Search input
├─────────────────────────┤
│  ┌───────────────────┐  │
│  │ 📄 Summary text   │  │ <- Saved item card
│  │    Jan 15     🔖  │  │
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ 📄 Summary text   │  │
│  │    Jan 14     🔖  │  │
│  └───────────────────┘  │
│                         │
├─────────────────────────┤
│  🏠  🕐  💾  ⚙️       │ <- Bottom navigation
└─────────────────────────┘
```

**Components:**

**Identical to History Screen** with these changes:

**Title:** "Saved"

**Empty State:**
- Icon: Bookmark icon (48dp)
- Title: "No Saved Items"
- Description: "Bookmark summaries to save them here"

**Saved Item Card:**
- Same as History Item but:
  - Delete button replaced with unsave button
  - Icon: Bookmark X (16dp)
  - Color: Gray-600
  - Hover: Gray-100 background

---

### 7. Settings Screen (Tab 4)

**Purpose:** App configuration and preferences

**Layout:**
```
┌─────────────────────────┐
│  Settings               │ <- Top bar
├─────────────────────────┤
│  ┌───────────────────┐  │
│  │ 🌐 Language       │  │ <- Language selector card
│  │    [Dropdown]     │  │
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ 📄 Summary Length │  │ <- Slider card
│  │    [Slider]       │  │
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ 🌙 Dark Mode  ⭘  │  │ <- Toggle card
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │  Summarize AI     │  │ <- About card
│  │  Version 1.0.0    │  │
│  └───────────────────┘  │
│                         │
├─────────────────────────┤
│  🏠  🕐  💾  ⚙️       │ <- Bottom navigation
└─────────────────────────┘
```

**Components:**

**Top Bar:**
- Height: 64dp
- Background: White
- Border bottom: 1dp solid Gray-200
- Title: "Settings"
  - Style: Heading2 (20sp, SemiBold)
  - Color: Gray-900
  - Padding: 24dp horizontal, 16dp vertical

**Content Area:**
- Background: Gray-50
- Padding: 24dp
- Scrollable: Vertical
- Spacing between cards: 16dp

**Settings Card (Base):**
- Width: Match parent
- Background: White
- Border: 1dp solid Gray-200
- Corner radius: 16dp
- Padding: 24dp
- Shadow: 2dp elevation

**Language Card:**
- Icon container:
  - Size: 48dp x 48dp
  - Background: Cyan-50
  - Corner radius: 12dp
  - Icon: Globe (24dp)
  - Icon color: Cyan-600
- Layout: Row with icon and content
- Content:
  - Label: "Language"
    - Style: BodyLarge (16sp, Medium)
    - Color: Gray-900
  - Description: "Choose your preferred language"
    - Style: BodySmall (12sp)
    - Color: Gray-500
    - Margin top: 4dp
  - Dropdown:
    - Width: Match parent
    - Height: 48dp
    - Background: White
    - Border: 1dp solid Gray-200
    - Corner radius: 12dp
    - Margin top: 12dp
    - Options: English, Spanish, French, German, Chinese

**Summary Length Card:**
- Icon container:
  - Background: Blue-50
  - Icon: File text (24dp)
  - Icon color: Blue-600
- Content:
  - Label: "Summary Length"
  - Description: "Adjust the default summary length"
  - Slider:
    - Width: Match parent
    - Track height: 4dp
    - Track color: Gray-200
    - Fill color: Cyan-600
    - Thumb size: 20dp
    - Thumb color: White with shadow
    - Margin top: 12dp
  - Labels row:
    - Layout: Row with space between
    - Left: "Short" (12sp, Gray-600)
    - Center: "50%" (12sp, Medium, Cyan-600)
    - Right: "Detailed" (12sp, Gray-600)
    - Margin top: 8dp

**Dark Mode Card:**
- Layout: Row with space between (center aligned)
- Icon container:
  - Background: Purple-50
  - Icon: Moon (24dp)
  - Icon color: Purple-600
- Content (flex: 1):
  - Label: "Dark Mode"
  - Description: "Toggle dark theme"
  - Padding: 0 16dp
- Switch:
  - Size: 48dp x 28dp
  - Track color (off): Gray-200
  - Track color (on): Cyan-600
  - Thumb size: 24dp
  - Thumb color: White

**About Card:**
- Background: Linear gradient (Cyan-50 to Blue-50)
- Text alignment: Center
- App name: "Summarize AI"
  - Style: Heading3 (18sp, SemiBold)
  - Color: Gray-900
- Version: "Version 1.0.0"
  - Style: BodySmall (12sp)
  - Color: Gray-600
  - Margin top: 8dp
- Tagline: "Turn long text into key insights instantly"
  - Style: BodySmall (12sp)
  - Color: Gray-500
  - Margin top: 12dp

---

## 🎯 Navigation Flow

```
Welcome Screen
      ↓
   [Get Started]
      ↓
┌─────────────────────────────┐
│    Bottom Tab Navigation    │
├─────────────────────────────┤
│ Home → History → Saved → Settings
│  ↓
│  [Summarize]
│  ↓
│  Loading Screen
│  ↓
│  Output Screen (Modal overlay)
│  ↓
│  [Back] returns to previous tab
│
│  Items saved automatically to History
│  [Save button] adds to Saved tab
└─────────────────────────────┘
```

---

## 🔄 Interactions & States

### Button States
1. **Normal:** Default appearance
2. **Pressed:** Scale down to 0.95x, slightly darker
3. **Disabled:** 50% opacity, no interaction
4. **Loading:** Show spinner, disable interaction

### Input States
1. **Empty:** Show placeholder
2. **Focused:** Border color changes to Cyan-600
3. **Filled:** Show text with clear button
4. **Error:** Border color Red-600, error message below

### Tab States
1. **Active:** Cyan-600 color, bolder icon (stroke width 2.5)
2. **Inactive:** Gray-400 color, normal icon (stroke width 2)

### Card Interactions
1. **Tap:** Navigate or expand
2. **Long press:** Show context menu (optional)
3. **Swipe:** Delete action (optional)

---

## 📱 Animations

### Screen Transitions
- **Fade + Slide:** 300ms, ease-out
- **Modal overlay:** Fade in from bottom, 250ms

### Loading Animation
- **Dots:** Sequential pulse, 1s duration, infinite
- **Progress bar:** Indeterminate sweep, 2s duration

### Button Press
- **Scale:** 95%, 100ms
- **Ripple:** Material design ripple effect

### List Items
- **Appear:** Stagger by 50ms per item
- **Delete:** Slide out right, 250ms

---

## 🛠️ Implementation Notes

### Jetpack Compose Components to Use

1. **Scaffold** - For screen structure with top bar and bottom nav
2. **LazyColumn** - For scrollable lists (History, Saved)
3. **TextField / OutlinedTextField** - For text input
4. **Button / OutlinedButton** - For action buttons
5. **Card** - For content containers
6. **TabRow** - For tab selector
7. **Slider** - For summary length control
8. **Switch** - For dark mode toggle
9. **DropdownMenu / ExposedDropdownMenuBox** - For language selection
10. **NavigationBar** - For bottom tab navigation
11. **Icon** - Using Material Icons
12. **CircularProgressIndicator / LinearProgressIndicator** - For loading
13. **ModalBottomSheet** - For output screen (alternative)

### Key Libraries
- **Material 3** - For design components
- **Compose Navigation** - For tab navigation
- **Coil** - For image loading (if needed)
- **DataStore** - For saving preferences
- **Room** - For local database (history storage)
- **ViewModel** - For state management
- **Hilt** - For dependency injection (optional)

### State Management
```kotlin
// Example ViewModel structure
class MainViewModel : ViewModel() {
    private val _summaryHistory = MutableStateFlow<List<SummaryItem>>(emptyList())
    val summaryHistory: StateFlow<List<SummaryItem>> = _summaryHistory
    
    private val _savedItems = MutableStateFlow<List<SummaryItem>>(emptyList())
    val savedItems: StateFlow<List<SummaryItem>> = _savedItems
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Functions for CRUD operations
}
```

---

## ✅ Checklist for Implementation

- [ ] Setup Jetpack Compose project with Material 3
- [ ] Create design system (colors, typography, spacing)
- [ ] Implement Welcome screen
- [ ] Implement bottom tab navigation
- [ ] Create Home/Input screen
- [ ] Create Loading screen with animation
- [ ] Create Output screen (modal or full screen)
- [ ] Create History screen with search
- [ ] Create Saved screen
- [ ] Create Settings screen with preferences
- [ ] Connect to AI API for summarization
- [ ] Implement local database for history
- [ ] Add file upload functionality
- [ ] Implement share functionality
- [ ] Add dark mode support (optional)
- [ ] Test on different screen sizes
- [ ] Polish animations and transitions

---

## 📸 Screenshots Reference

All the visual references are available in the React web app. You can:
1. Take screenshots of each screen from the browser
2. Use the current showcase view to see all screens at once
3. Export screenshots for your design documentation

---

## 🎨 Design Assets Needed

- App icon (1024x1024 PNG)
- Splash screen graphic
- AI/Document illustration for welcome screen
- Export icon for upload button
- Any custom icons not in Material Icons

---

## 📝 Additional Notes

- All spacing values should scale with screen size
- Use Material 3 components for consistency
- Follow Android design guidelines for gestures
- Ensure accessibility (touch targets, contrast ratios)
- Support both portrait and landscape (optional for v1)
- Consider tablet layout adaptations for larger screens

---

**End of Design Specification**

This document should be sufficient for Cursor AI or any developer to implement the Android app in Jetpack Compose while maintaining the exact design from this React/Tailwind prototype.