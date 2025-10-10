# Testing the Hybrid Extraction Implementation

## Quick Start Testing

### Test URLs by Strategy

#### Should Succeed with Strategy 1 (Readability4J)
✅ **Wikipedia**
```
https://en.wikipedia.org/wiki/Artificial_intelligence
```
- Expected: SUCCESS in 2-5 seconds
- Look for: "✅ SUCCESS with Readability4J"

✅ **BBC News**
```
https://www.bbc.com/news/technology
```
- Expected: SUCCESS in 2-5 seconds  
- Look for: "✅ SUCCESS with Readability4J"

✅ **The Guardian**
```
https://www.theguardian.com/technology
```
- Expected: SUCCESS in 3-6 seconds
- Look for: "✅ SUCCESS with Readability4J"

#### Should Succeed with Strategy 2 (Jina AI)
✅ **stuff.co.nz** (Previously failed!)
```
https://www.stuff.co.nz/nz-news/360849852/severe-weather-main-centre-42-hour-long-strong-wind-watch
```
- Expected: Strategy 1 fails → Strategy 2 SUCCESS in 3-8 seconds
- Look for: "⚠ Readability4J failed" → "✅ SUCCESS with Jina AI"

✅ **JavaScript-heavy SPA**
```
https://www.nytimes.com (or other JS-heavy site)
```
- Expected: Strategy 1 fails → Strategy 2 SUCCESS
- Look for: "✅ SUCCESS with Jina AI"

#### Should Need Strategy 3 (Enhanced Jsoup)
Some sites may work better with Jsoup's meta tags:
```
https://medium.com/@author/article (varies by article)
```

## What to Watch in Logcat

### Successful Extraction (Strategy 1)
```
E  ═══ HYBRID EXTRACTION START ═══
E  URL: https://www.bbc.com/news/article
E  ✓ URL validated successfully
E  → Strategy 1: Trying Readability4J...
E    ✓ Readability extracted - Title: Article Title, Content: 2500 chars
E  ✅ SUCCESS with Readability4J - Length: 2500
E  ═══ EXTRACTION COMPLETE ═══
```

### Fallback to Strategy 2 (stuff.co.nz case)
```
E  ═══ HYBRID EXTRACTION START ═══
E  URL: https://www.stuff.co.nz/...
E  ✓ URL validated successfully
E  → Strategy 1: Trying Readability4J...
E  ⚠ Readability4J failed: Readability timeout: Read timeout
E  → Strategy 2: Trying Jina AI...
E    → Calling Jina AI: https://r.jina.ai/https://www.stuff.co.nz/...
E    ✓ Jina AI extracted - Content: 3200 chars
E  ✅ SUCCESS with Jina AI - Length: 3200
E  ═══ EXTRACTION COMPLETE ═══
```

### All Strategies Fail (Rare)
```
E  ═══ HYBRID EXTRACTION START ═══
E  URL: https://paywalled-site.com/...
E  → Strategy 1: Trying Readability4J...
E  ⚠ Readability4J failed: No content extracted
E  → Strategy 2: Trying Jina AI...
E  ⚠ Jina AI failed: Jina AI returned 403
E  → Strategy 3: Trying Enhanced Jsoup...
E  ⚠ Enhanced Jsoup failed: Content too short: 20 chars
E  ❌ ALL STRATEGIES FAILED
E  ═══ EXTRACTION FAILED ═══
```

## Testing Checklist

### Basic Functionality
- [ ] Share Wikipedia link → Extracts and summarizes successfully
- [ ] Share BBC News link → Extracts and summarizes successfully
- [ ] **Share stuff.co.nz link → NOW WORKS (via Jina AI)**
- [ ] Share Medium article → Extracts and summarizes successfully

### Navigation Flow
- [ ] Share URL when app closed → Auto-navigates to StreamingOutputScreen
- [ ] Share URL when app running → Auto-navigates to StreamingOutputScreen
- [ ] Streaming starts automatically
- [ ] Back button returns to HomeScreen with extracted content in text field

### Error Handling
- [ ] Share paywalled content → Shows orange error card with helpful tip
- [ ] No network connection → Shows "No network connection available"
- [ ] Invalid URL → Shows clear error message
- [ ] Error card has helpful manual paste tip

### Performance
- [ ] Strategy 1 success → Completes in 2-5 seconds
- [ ] Strategy 2 fallback → Completes in 5-15 seconds total
- [ ] Strategy 3 fallback → Completes in 10-60 seconds total
- [ ] Multiple URLs in succession work correctly

## Expected Results by Site

| Website | Strategy | Time | Notes |
|---------|----------|------|-------|
| Wikipedia | 1 (Readability) | 2-3s | Fast, clean extraction |
| BBC News | 1 (Readability) | 2-4s | Excellent structure |
| Guardian | 1 (Readability) | 3-5s | Good article markup |
| **stuff.co.nz** | **2 (Jina AI)** | **3-8s** | **Now works!** 🎉 |
| Medium | 1 or 2 | 2-6s | Varies by article |
| Reuters | 1 (Readability) | 2-4s | Clean markup |
| NYTimes | 2 (Jina AI) | 4-8s | JavaScript-heavy |

## Debug Commands

### Filter Logcat for Extraction
```bash
adb logcat -s WebContentExtractor:E
```

### Watch for Success/Failure
```bash
adb logcat | grep -E "(SUCCESS|FAILED|Strategy)"
```

### Full Extraction Flow
```bash
adb logcat | grep -E "(═══|→|✓|✅|❌|⚠)"
```

## Known Issues & Solutions

### Issue: "Jina AI returned 429"
**Cause:** Rate limit exceeded (unlikely with 1M/month free tier)
**Solution:** Falls back to Strategy 3, or shows manual paste tip

### Issue: "Readability timeout"
**Cause:** Website very slow to respond
**Solution:** Automatically tries Jina AI next - usually succeeds

### Issue: "All strategies failed"
**Cause:** Paywalled, heavily protected, or broken URL
**Solution:** User sees helpful tip to manually copy/paste

## Success Criteria

✅ **90%+ of shared URLs extract successfully**
✅ **Most extractions complete in < 10 seconds**
✅ **stuff.co.nz now works reliably**
✅ **Clear error messages when extraction fails**
✅ **Automatic navigation to StreamingOutputScreen**
✅ **No app crashes or network errors**

## Regression Testing

Ensure existing functionality still works:
- [ ] Manual text input and summarization
- [ ] PDF/DOC upload
- [ ] History screen
- [ ] Saved summaries
- [ ] Settings (streaming toggle)
- [ ] All navigation flows

## Next Steps After Testing

1. **Gather metrics:**
   - Which strategy succeeds most often?
   - Average extraction time?
   - Most common failure reasons?

2. **User feedback:**
   - Do users notice the improvement?
   - Are error messages helpful?
   - Any specific sites still failing?

3. **Optimization:**
   - Adjust strategy order if needed
   - Fine-tune timeouts
   - Add more specific error handling

## Quick Test Script

Share these URLs one by one and verify success:

1. https://en.wikipedia.org/wiki/Machine_learning ✅ Strategy 1
2. https://www.bbc.com/news (any article) ✅ Strategy 1
3. https://www.stuff.co.nz/nz-news/360849852/severe-weather-main-centre-42-hour-long-strong-wind-watch ✅ Strategy 2
4. https://www.theguardian.com/technology (any article) ✅ Strategy 1

Expected: **All 4 should succeed and auto-navigate to streaming screen!**

