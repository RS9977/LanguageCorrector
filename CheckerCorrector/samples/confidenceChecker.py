import json
import language_tool_python

def get_error_confidence(sentence):
    # Initialize LanguageTool
    tool = language_tool_python.LanguageTool('en-US')

    # Tokenize the sentence
    sentences = sentence.split(".")

    # Initialize dictionaries to store confidence scores for sentences and phrases
    sentence_confidence = {}
    phrase_confidence = {}

    # Iterate over each sentence
    for sent in sentences:
        # Calculate confidence score for the sentence
        sent_confidence = {"text": sent.strip()}
        
        # Check for errors in the entire sentence
        sent_matches = tool.check(sent.strip())
        sent_confidence["confidence"] = min(len(sent_matches) * 100 / max(len(sent.split()), 1), 100)

        # Add confidence score for the sentence
        sentence_confidence[sent.strip()] = sent_confidence["confidence"]

        # Generate phrases of lengths 2 to 4 and calculate confidence scores
        words = sent.strip().split()
        for length in range(2, 5):
            for i in range(len(words) - length + 1):
                phrase = ' '.join(words[i:i+length])
                # Check for errors in the phrase
                matches = tool.check(phrase)
                # Calculate confidence score
                confidence_score = len(matches) * 100 / max(len(phrase.split()), 1)
                # Add confidence score for the phrase
                phrase_confidence[phrase] = min(confidence_score, 100)

    return sentence_confidence, phrase_confidence

# Read input from checker.txt
with open("checker.txt", "r") as file:
    input_text = file.read()

# Get confidence scores
sentences_confidence, phrases_confidence = get_error_confidence(input_text)

# Create JSON structure
output_json = {"sentences": sentences_confidence, "phrases": phrases_confidence}

# Save to a JSON file
with open("confidence__pyhton3rdParty.json", "w") as json_file:
    json.dump(output_json, json_file, indent=2)

print("JSON file saved successfully.")
