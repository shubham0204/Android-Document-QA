# Document Question-Answering with Local RAG in Android

> A simple Android app that allows the user to add a PDF/DOCX document and ask natural-language questions whose 
> answers are generated by the means of an LLM

https://github.com/shubham0204/Android-Document-QA/assets/41076823/d4d6a21c-a29e-4429-9d43-ca38785c5b50

(The PDF used in the demo can be found in [resources](https://github.com/shubham0204/Android-Document-QA/tree/main/resources) directory)

[YT Video](https://youtu.be/Av0N40Weu0M?feature=shared)

## Goals

- Demonstrate the collective use of an on-device vector database, embeddings model and a custom text-splitter to build a retrieval-augmented generation (RAG) based pipeline for simple document question-answering
- Use modern Android development practices and recommended architecture guidelines
- Explore and suggest better tools/alternatives for building fully offline, on-device RAG pipeline for Android with minimum compute and storage requirements

| Feature            | On-Device | Remote |
|--------------------|-----------|--------|
| Sentence Embedding | ✅         |        |
| Text Splitter      | ✅         |        |
| Vector Database    | ✅         |        |
| LLM                |           | ✅      |

## Setup

1. Clone the `main` branch, 

```bash
$> git clone --depth=1 https://github.com/shubham0204/Android-Document-QA
```

2. Open the resulting directory in Android Studio. A project build is initiated automatically, if not, run `./gradlew :app:build` in the terminal.

3. [Get an API key from Google AI Studio](https://ai.google.dev/gemini-api/docs/api-key) to use the Gemini API.

4. [Run the app](https://developer.android.com/studio/run) on a physical device or a emulator. Tap on the '🔑' icon to add the API key.


Perform a Gradle sync, and run the application. 

## Tools

1. [Apache POI](https://poi.apache.org/) and [iTextPDF](https://github.com/itext/itextpdf) for parsing DOCX and PDF documents
2. [ObjectBox](https://objectbox.io/) for on-device vector-store and NoSQL database
3. [Sentence Embeddings (`all-MiniLM-L6-V2`)](https://github.com/shubham0204/Sentence-Embeddings-Android) for generating on-device text/sentence embeddings
4. [Gemini Android SDK](https://developer.android.com/ai/google-ai-client-sdk) as a hosted large-language model (Uses Gemini-1.5-Flash)

## Working 

The basic working flow on the app is as follows:

1. When the user selects a PDF/DOCX document (the only ones which can be imported for now), the text is parsed with 
the libraries mentioned in (1) of [Tools](#tools). See [PDFReader.kt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/java/com/ml/shubham0204/docqa/domain/readers/PDFReader.kt) and [DOCXReader.kt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/java/com/ml/shubham0204/docqa/domain/readers/DOCXReader.kt) for reference.
2. Chunks or overlapping sub-sequences are produced from the text, given the size of sequence (`chunkSize`) and 
the extent of overlap between two sequences (`chunkOverlap`). See [WhiteSpaceSplitter.kt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/java/com/ml/shubham0204/docqa/domain/splitters/WhiteSpaceSplitter.kt) for reference.
3. Each chunk is encoded into a fixed-size vector i.e. a text embedding. The embeddings are inserted in the vector database, with each chunk/embedding having a distinct `chunkId`. See [SentenceEmbeddingProvider.kt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/java/com/ml/shubham0204/docqa/domain/embeddings/SentenceEmbeddingProvider.kt) for reference.
4. When the user submits a query, we find the top-K most similar chunks from the database by comparing their embeddings.
5. The chunks corresponding to the nearest embeddings are injected into a pre-built prompt along with the query, which is provided to the LLM. The LLM generates a well-formed natural language answer to the user's query. See [GeminiRemoteAPI.kt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/java/com/ml/shubham0204/docqa/domain/llm/GeminiRemoteAPI.kt) for reference.

See the [prompt](https://github.com/shubham0204/Android-Document-QA/blob/main/app/src/main/res/values/strings.xml),

```text
You are an intelligent search engine. You will be provided with some retrieved context, as well as the users query.
Your job is to understand the request, and answer based on the retrieved context.
Strictly Use ONLY the following pieces of context to answer the question at the end.
Provide only the answer as a response

Here is the retrieved context:
    $CONTEXT

Here is the users query:
    $QUERY
```

## Discussion

### Why not use on-device LLMs instead of the Gemini's Cloud SDK?

Using an on-device LLM is possible in Android, but at the expense of a large app size (>1GB) and compute requirements. Google's Edge AI SDK has some options where models like Gemma, MS Phi-2, Falcon can be used completely on-device and accessed via Mediapipe's Android/iOS/Web APIs. See the [official documentation for Mediapipe LLM Inference](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference), it also includes instructions ofr LoRA fine-tuning.

Moreover, the same docs [specific for Android](https://ai.google.dev/edge/mediapipe/solutions/genai/llm_inference/android) mention the fact,

> During development, you can use adb to push the model to your test device for a simpler workflow. For deployment, host the model on a server and download it at runtime. The model is too large to be bundled in an APK.

The integration using Mediapipe LLM inference API is easy. Due to the absence of a good Android device, I went ahead with the Cloud API, but it would be great to have an on-device option. [Gemini Nano](https://ai.google.dev/gemini-api/docs/get-started/android_aicore) currently available on limited devices is also an on-device solution.

Other tools for using LLMs on Android:

1. [mlc](https://llm.mlc.ai/docs/deploy/android.html) (Also see [Llama3 on Android](https://github.com/NSTiwari/Llama3-on-Mobile))
2. [llama.cpp for Android](https://github.com/JackZeng0208/llama.cpp-android-tutorial)

### (Solved) Better alternatives for the Universal Sentence Encoder (embedding model)

#### Problem: 
The app currently uses the [Universal Sentence Encoder](https://www.kaggle.com/models/google/universal-sentence-encoder) model from Google, as it was the only possible way to generate text/sentence embeddings on an Android device, with a builtin API and tokenizer. It generates an embedding of size 100. 

After checking the retrieved context (similar chunks) for a few questions, I recognized that the embedding model was not able to understand the context of the sentence to a significant extent. I couldn't find a metric to validate this point on [HuggingFace's MTEB](https://huggingface.co/spaces/mteb/leaderboard). Models such as [sentence-transformers](https://huggingface.co/sentence-transformers) were particular great at understanding context, but their integration in an Android app remains an open problem.

#### Solution

The `all-MiniLM-L2-V6` model from [sentence-transformers](https://huggingface.co/sentence-transformers) has been ported to Android with the help of ONNX/onnxruntime and the Rust-implementation of [huggingface/tokenziers](https://github.com/huggingface/tokenizers). See the app's [assets](https://github.com/shubham0204/Android-Document-QA/tree/main/app/src/main/assets) folder to find the ONNX model `tokenizer.json`

See the main repository [shubham0204/Sentence-Embeddings-Android](https://github.com/shubham0204/Sentence-Embeddings-Android) for more details.

## Contributions and Open Problems

Feel free to [raise an issue](https://github.com/shubham0204/Android-Document-QA/issues/new) or [open a PR](https://github.com/shubham0204/Android-Document-QA/pulls). The following can be improved in the app:

1. Add on-device LLM capabilities
2. Build a new text-splitter, taking inspiration from Langchain or LlamaIndex
3. Port a good, small-in-size embedding model from HuggingFace to work in the app
