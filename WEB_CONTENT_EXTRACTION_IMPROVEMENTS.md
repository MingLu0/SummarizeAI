# Web Content Extraction Improvements

## Problem
`fetchDocument` was unable to get meaningful content from URLs, especially modern news websites that:
- Load content dynamically with JavaScript (which Jsoup cannot execute)
- Have complex HTML structures
- Use anti-scraping measures
- Rely on meta tags for content summaries

## Solution - Multi-Strategy Content Extraction

### 1. Enhanced Content Extraction Strategy (Priority Order)

#### Strategy 1: Meta Tag Extraction
Extracts content from social media meta tags (most reliable for modern websites):
- **Open Graph** (`og:description`) - Used by Facebook, LinkedIn
- **Twitter Cards** (`twitter:description`) - Used by Twitter
- **Standard Meta Description** - Fallback meta tag

**Why this works:** Modern news sites always populate these tags for social media sharing, even if main content is JavaScript-rendered.

#### Strategy 2: Enhanced Content Selectors
Expanded from 9 to 17 content selectors including:
- `article[role='article']`, `article.post`, `article.article`
- `div.article-body`, `div.story-body`, `div.post-body`
- `div[itemprop='articleBody']` (schema.org markup)
- Standard selectors: `article`, `main`, `[role='main']`
- Legacy selectors: `.content`, `.post-content`, `#content`

**Improvement:** More variations to match different website structures.

#### Strategy 3: Paragraph Extraction
Extracts substantial paragraphs when structured content not found:
- Filters out short paragraphs (< 50 chars) - likely navigation/ads
- Takes first 20 substantial paragraphs
- Joins with double newlines for readability

**Fallback:** Better than getting nothing from JavaScript-heavy sites.

### 2. Enhanced Title Extraction
Multi-source title extraction with priority:
1. `og:title` meta tag
2. `twitter:title` meta tag  
3. First `<h1>` element
4. `<title>` tag
5. "Untitled" as final fallback

### 3. Improved HTTP Request Headers
Added browser-like headers to avoid being blocked:
```kotlin
.userAgent(USER_AGENT)
.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
.header("Accept-Language", "en-US,en;q=0.9")
.header("Accept-Encoding", "gzip, deflate, br")
.header("Connection", "keep-alive")
.header("Upgrade-Insecure-Requests", "1")
```

**Why this helps:** Makes requests look more like real browsers, reducing the chance of being blocked.

### 4. Better Error Handling & Logging

#### Comprehensive Logging
Added detailed logs at each extraction stage:
- "Starting content extraction for: {url}"
- "URL validated successfully"
- "Document fetched successfully, parsing content..."
- "Extracted from {source}, length: {length}"
- "Successfully extracted content. Title: {title}, Content length: {length}"

#### Specific Error Messages
- Timeout errors: "Request timed out while extracting content from the webpage"
- Content too short: "No readable content found on the page. The website may require JavaScript or use dynamic loading."
- Validation errors: "Failed to extract content: {specific error}"

### 5. Minimum Content Threshold
Validates that extracted content is at least 100 characters:
```kotlin
if (cleanedText.isBlank() || cleanedText.length < 100) {
    throw IllegalStateException("No readable content found...")
}
```

**Why:** Ensures we get meaningful content, not just navigation text.

## How It Handles Different Websites

### Modern News Sites (like stuff.co.nz)
1. ✅ Tries meta tags first → **Most likely to succeed**
2. If fails → Tries content selectors
3. If fails → Extracts paragraphs
4. If still fails → Clear error message

### Blog Sites
1. Tries meta tags (may or may not exist)
2. ✅ Content selectors → **Most likely to succeed** (`article`, `.post-content`)
3. Fallback to paragraphs if needed

### Simple HTML Sites
1. May skip meta tags
2. ✅ Content selectors or paragraph extraction → **Will succeed**

### JavaScript-Heavy SPAs
1. ✅ Meta tags only reliable option → **Best chance**
2. Other strategies will fail (no server-side rendered content)
3. Clear error message explains JavaScript issue

## Expected Behavior

### Success Cases
- Website has meta description → Extracts 100-500 char summary
- Website has article markup → Extracts full article content
- Website has paragraphs → Extracts combined paragraph text

### Failure Cases
- **Pure JavaScript app with no meta tags** → Error: "The website may require JavaScript or use dynamic loading"
- **Content too short** → Error: "No readable content found on the page"
- **Network timeout** → Error: "Request timed out while extracting content from the webpage"

## Testing Strategy

### Good Test URLs (Should Work)
- News sites: BBC, Guardian, Reuters (have meta tags)
- Medium articles (good meta tags + content selectors)
- Wikipedia (excellent HTML structure)
- Blog platforms (WordPress, Ghost)

### Challenging URLs (May Fail)
- Single Page Apps without SSR
- Paywalled content
- Sites requiring JavaScript for all content
- Sites with heavy anti-scraping measures

## Limitations

### What Jsoup Can't Do
- ❌ Execute JavaScript
- ❌ Handle dynamic content loading
- ❌ Interact with pages (click buttons, scroll)
- ❌ Bypass sophisticated bot detection

### Alternative Solutions (Future)
If current solution insufficient:
1. Use a web scraping API service (ScrapingBee, ScrapingAnt)
2. Implement server-side rendering solution
3. Use article extraction API (Diffbot, Mercury)
4. Allow users to paste text manually when extraction fails

## Debug Workflow

When extraction fails, check logs for:
1. "Fetching document from: {url}" → Did fetch start?
2. "Document fetched successfully" → Did HTTP request work?
3. "Extracted from {source}" → Which strategy found content?
4. "Content length: {length}" → How much content was found?

If content length < 100 → Website likely requires JavaScript, suggest manual paste to user.

