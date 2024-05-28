# Document Question Answering with Local RAG in Android

## Setup

1. Clone the `main` branch, 

```bash
$> git clone --depth=1 https://github.com/shubham0204/Android-Document-QA
```

2. [Get an API key from Google AI Studio](https://ai.google.dev/gemini-api/docs/api-key) to use the Gemini API. Copy 
the key and paste it in `local.properties` present in the root directory of the project,

```
geminiKey="AIza[API_KEY_HERE]"
```

Perform a Gradle sync, and run the application. 

## Tools

- [Apache POI](https://poi.apache.org/) and [iTextPDF](https://github.com/itext/itextpdf) for parsing DOCX and PDF documents
- [ObjectBox](https://objectbox.io/) for on-device vector-store and NoSQL database
- [Mediapipe Text Embedding](https://ai.google.dev/edge/mediapipe/solutions/text/text_embedder/android) for generating on-device text/sentence embeddings
- [Gemini Android SDK](https://developer.android.com/ai/google-ai-client-sdk) as a hosted large-language model