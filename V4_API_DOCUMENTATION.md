# V4 API Technical Documentation for Android Integration

## Overview

V4 is a **streaming structured summarization API** that uses **Server-Sent Events (SSE)** to deliver real-time updates as the AI model generates a structured summary. The API supports two input modes (URL or text) and returns structured data in NDJSON patch format.

## Base URL

```
https://colin730-summarizerapp.hf.space/api/v4
```

## Endpoint

```
POST /scrape-and-summarize/stream-ndjson
```

## Request Format

### HTTP Headers
```
Content-Type: application/json
```

### Request Body (JSON)

```json
{
  "url": "https://example.com/article",  // Optional: URL to scrape and summarize
  "text": "Your article text here...",   // Optional: Direct text to summarize
  "style": "executive",                  // Required: "skimmer", "executive", or "eli5"
  "max_tokens": 256,                      // Optional: 128-2048 (default: 256)
  "include_metadata": true,               // Optional: Include scraping metadata (default: true)
  "use_cache": true                       // Optional: Use cached content for URLs (default: true)
}
```

**Important Constraints:**
- You must provide **either** `url` **OR** `text`, but **not both**
- `url`: Must be a valid HTTP/HTTPS URL (not localhost or private IPs)
- `text`: Must be 50-50,000 characters, not mostly whitespace
- `style`: One of `"skimmer"`, `"executive"`, or `"eli5"`

## Response Format: Server-Sent Events (SSE)

The API returns a **streaming response** using Server-Sent Events (SSE) protocol.

### Response Headers
```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive
X-Accel-Buffering: no
```

### Event Format

Each event follows the SSE format:
```
data: <JSON_OBJECT>\n\n
```

### Event Types

#### 1. Metadata Event (First Event, if `include_metadata=true`)

```json
{
  "type": "metadata",
  "data": {
    "input_type": "url",              // or "text"
    "url": "https://example.com/...", // Only if input_type is "url"
    "title": "Article Title",          // Only if input_type is "url"
    "author": "Author Name",           // Only if input_type is "url"
    "date": "2024-01-01",             // Only if input_type is "url"
    "site_name": "Example Site",      // Only if input_type is "url"
    "scrape_method": "static",       // Only if input_type is "url"
    "scrape_latency_ms": 1500.5,     // Only if input_type is "url"
    "extracted_text_length": 5000,   // Only if input_type is "url"
    "text_length": 5000,               // Only if input_type is "text"
    "style": "executive"
  }
}
```

#### 2. Patch Events (Streaming Updates)

Each patch event represents a change to the structured summary:

```json
{
  "delta": {
    "op": "set",                    // or "append" or "done"
    "field": "title",               // Field name (only for "set" and "append")
    "value": "Article Title"        // Field value (only for "set" and "append")
  },
  "state": {                        // Current complete state after applying this patch
    "title": "Article Title",
    "main_summary": null,
    "key_points": [],
    "category": null,
    "sentiment": null,
    "read_time_min": null
  },
  "done": false,                    // true when generation is complete
  "tokens_used": 15                 // Number of tokens generated so far
}
```

#### 3. Final Event (Completion)

```json
{
  "delta": null,
  "state": {                        // Final complete state
    "title": "Article Title",
    "main_summary": "Summary text...",
    "key_points": ["Point 1", "Point 2", "Point 3"],
    "category": "Tech",
    "sentiment": "neutral",
    "read_time_min": 5
  },
  "done": true,
  "tokens_used": 97,
  "latency_ms": 8000.78             // Total generation time in milliseconds
}
```

#### 4. Error Event

```json
{
  "delta": null,
  "state": null,
  "done": true,
  "tokens_used": 0,
  "error": "Error message here"
}
```

## Patch Operations

### Operation Types

1. **`"set"`**: Set or overwrite a scalar field
   ```json
   {"op": "set", "field": "title", "value": "My Article Title"}
   {"op": "set", "field": "category", "value": "Tech"}
   {"op": "set", "field": "sentiment", "value": "neutral"}
   {"op": "set", "field": "read_time_min", "value": 5}
   ```

2. **`"append"`**: Add an item to the `key_points` array
   ```json
   {"op": "append", "field": "key_points", "value": "First key point"}
   {"op": "append", "field": "key_points", "value": "Second key point"}
   ```

3. **`"done"`**: Signal that generation is complete
   ```json
   {"op": "done"}
   ```

## State Structure

The `state` object contains the complete structured summary:

```typescript
interface StructuredSummary {
  title: string | null;              // 6-10 words, concise title
  main_summary: string | null;       // 2 sentences maximum
  key_points: string[];              // 3-5 items, each 8-12 words
  category: string | null;           // 1-2 words (e.g., "Tech", "Crime")
  sentiment: "positive" | "negative" | "neutral" | null;
  read_time_min: number | null;      // Estimated reading time in minutes
}
```

## Complete Example Flow

### Request
```http
POST /api/v4/scrape-and-summarize/stream-ndjson
Content-Type: application/json

{
  "url": "https://www.example.com/article",
  "style": "executive",
  "max_tokens": 256,
  "include_metadata": true
}
```

### Response Stream

```
data: {"type":"metadata","data":{"input_type":"url","url":"https://www.example.com/article","title":"Example Article","scrape_latency_ms":1500.5,"style":"executive"}}

data: {"delta":{"op":"set","field":"title","value":"Executives React to Couple's Murder Trial Outcome"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":null,"key_points":[],"category":null,"sentiment":null,"read_time_min":null},"done":false,"tokens_used":12}

data: {"delta":{"op":"set","field":"main_summary","value":"High Court finds couple not guilty in Faatoia murder case."},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":[],"category":null,"sentiment":null,"read_time_min":null},"done":false,"tokens_used":49}

data: {"delta":{"op":"set","field":"category","value":"Law & Order"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":[],"category":"Law & Order","sentiment":null,"read_time_min":null},"done":false,"tokens_used":57}

data: {"delta":{"op":"set","field":"sentiment","value":"neutral"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":[],"category":"Law & Order","sentiment":"neutral","read_time_min":null},"done":false,"tokens_used":63}

data: {"delta":{"op":"append","field":"key_points","value":"No clear motive for Faatoia's attack"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":["No clear motive for Faatoia's attack"],"category":"Law & Order","sentiment":"neutral","read_time_min":null},"done":false,"tokens_used":74}

data: {"delta":{"op":"append","field":"key_points","value":"Defensive actions led to fatal outcome"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":["No clear motive for Faatoia's attack","Defensive actions led to fatal outcome"],"category":"Law & Order","sentiment":"neutral","read_time_min":null},"done":false,"tokens_used":85}

data: {"delta":{"op":"append","field":"key_points","value":"Potential for further legal disputes"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":["No clear motive for Faatoia's attack","Defensive actions led to fatal outcome","Potential for further legal disputes"],"category":"Law & Order","sentiment":"neutral","read_time_min":null},"done":false,"tokens_used":95}

data: {"delta":{"op":"done"},"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":["No clear motive for Faatoia's attack","Defensive actions led to fatal outcome","Potential for further legal disputes"],"category":"Law & Order","sentiment":"neutral","read_time_min":3},"done":true,"tokens_used":97}

data: {"delta":null,"state":{"title":"Executives React to Couple's Murder Trial Outcome","main_summary":"High Court finds couple not guilty in Faatoia murder case.","key_points":["No clear motive for Faatoia's attack","Defensive actions led to fatal outcome","Potential for further legal disputes"],"category":"Law & Order","sentiment":"neutral","read_time_min":3},"done":true,"tokens_used":97,"latency_ms":8000.78}
```

## Android Implementation Notes

### 1. SSE Client Library

Use a library that supports Server-Sent Events:
- **OkHttp** with SSE support (via `EventSource` or similar)
- **Retrofit** with custom SSE converter
- **Ktor** with SSE client support

### 2. Parsing SSE Events

1. Read the stream line by line
2. Look for lines starting with `data: `
3. Extract the JSON after `data: `
4. Parse the JSON object
5. Handle each event type accordingly

### 3. State Management

- **Initialize** an empty state object
- **Update** state as each patch event arrives
- **Display** the current state in the UI (real-time updates)
- **Finalize** when `done: true` is received

### 4. Error Handling

- Network errors: Connection timeout, no internet
- HTTP errors: 400 (bad request), 422 (validation error), 502 (scraping failed)
- Stream errors: Parse errors, incomplete JSON
- Always check for `error` field in events

### 5. UI Updates

- Show loading indicator while `done: false`
- Update UI incrementally as patches arrive:
  - Title appears first
  - Summary appears next
  - Key points append one by one
  - Category and sentiment appear
  - Read time appears last
- Show completion when `done: true`

### 6. Performance Considerations

- **Streaming**: Updates arrive in real-time (~8-10 seconds total)
- **Token count**: Monitor `tokens_used` for progress indication
- **Latency**: Final event includes `latency_ms` for metrics
- **Caching**: Use `use_cache: true` for repeated URL requests

## HTTP Status Codes

- **200 OK**: Stream started successfully
- **400 Bad Request**: Invalid request body (missing url/text, invalid style, etc.)
- **422 Unprocessable Entity**: Validation error (URL too short, text too long, etc.)
- **502 Bad Gateway**: Scraping failed (site blocked, paywall, etc.)
- **500 Internal Server Error**: Server error

## Testing

### Test with cURL

```bash
curl -X POST "https://colin730-summarizerapp.hf.space/api/v4/scrape-and-summarize/stream-ndjson" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Artificial intelligence is transforming healthcare. Machine learning algorithms can now detect diseases earlier than human doctors.",
    "style": "executive",
    "max_tokens": 256
  }' \
  --no-buffer
```

### Test with URL

```bash
curl -X POST "https://colin730-summarizerapp.hf.space/api/v4/scrape-and-summarize/stream-ndjson" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.example.com/article",
    "style": "skimmer",
    "include_metadata": true
  }' \
  --no-buffer
```

## Summary

V4 API is a **streaming NDJSON patch-based API** that:
1. Accepts URL or text input
2. Returns Server-Sent Events (SSE) stream
3. Sends incremental updates as JSON patches
4. Maintains complete state in each event
5. Completes in ~8-10 seconds with real-time UI updates

The Android app should:
- Use an SSE-capable HTTP client
- Parse `data: ` lines from the stream
- Apply patches to maintain state
- Update UI incrementally
- Handle errors gracefully

