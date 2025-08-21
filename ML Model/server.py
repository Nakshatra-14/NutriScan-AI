from flask import Flask, request, jsonify
import joblib
import numpy as np
import random

# Initialize the Flask application
app = Flask(__name__)
# Ensure JSON responses are not ASCII-encoded to support non-English characters
app.config['JSON_AS_ASCII'] = False

# --- Load Models and Encoders (Done once at startup) ---
try:
    model = joblib.load("model_v2.joblib")
    age_encoder = joblib.load("age_encoder_v2.joblib")
    label_encoder = joblib.load("label_encoder_v2.joblib")
except FileNotFoundError as e:
    print(f"Fatal Error: Could not load model files. Details: {e}")
    exit()

# --- Helper Functions for Scoring ---
def calculate_calories(protein, carb, fat):
    return (protein * 4) + (carb * 4) + (fat * 9)

def calculate_nutri_score(protein, fiber, fat, sugar, calories):
    energy_points = max(0, min(10, int(calories / 335)))
    sugar_points = max(0, min(10, int(sugar / 4.5)))
    fat_points = max(0, min(10, int(fat / 1)))
    fiber_points = max(0, min(5, int(fiber / 0.9)))
    protein_points = max(0, min(5, int(protein / 1.6)))
    return (energy_points + sugar_points + fat_points) - (fiber_points + protein_points)

# --- THE CORE MULTILINGUAL AI BRAIN ---
def generate_multilingual_analysis(product_name, data, language):
    """
    Generates the professional analysis in the requested language.
    """
    response = {}
    gi = data['glycemic_index']
    nutri_score = data['nutri_score']
    eco_score = data['eco_score']
    
    # --- Language Translations ---
    # We use a dictionary to hold all text templates for each language
    text_templates = {
        "en": {
            "good_title": "A Good Choice",
            "mindful_title": "A Mindful Choice",
            "good_summary": f"After analyzing '{product_name}', our conclusion is that it's a nutritionally sound option that fits well within a balanced diet.",
            "mindful_summary": f"Thank you for scanning '{product_name}'. Our analysis suggests this is a food to be enjoyed in moderation. Let's explore the details.",
            "gi_low": f"The Glycemic Index is low, around {gi:.0f}. This is excellent, as it means this food will likely provide you with steady, sustained energy.",
            "gi_moderate": f"This food has a moderate Glycemic Index of about {gi:.0f}, meaning it's best consumed as part of a balanced meal to ensure stable energy.",
            "gi_high": f"With a high Glycemic Index of approximately {gi:.0f}, this food will release sugar into your bloodstream quickly. This can provide a fast energy boost, but may also lead to a crash later on.",
            "nutri_good": f"The Nutri-Score is {nutri_score}, which is a top-tier rating. This indicates a high nutritional quality, making it a very healthy choice.",
            "nutri_ok": f"A Nutri-Score of {nutri_score} is quite good. It represents a solid, everyday food item.",
            "nutri_bad": f"The Nutri-Score is {nutri_score}, which suggests it's less nutritionally dense, likely due to higher levels of sugar, salt, or saturated fat.",
            "eco_good": f"From a sustainability perspective, this item scores well with a low Eco-Score of about {eco_score:.0f}, indicating a smaller environmental footprint.",
            "eco_bad": f"The Eco-Score is around {eco_score:.0f}. This suggests a moderate to high environmental impact, often associated with its ingredients.",
            "pro_tip_good": "To get the most from your food, always consider the meal as a whole. Building a plate with a variety of colors and nutrients is key to a healthy lifestyle.",
            "pro_tip_bad": "Here is a powerful tip. To counteract the high Glycemic Index, pair this food with a source of fiber, like a side of vegetables or a handful of nuts. This can help slow down the sugar absorption and create a more balanced metabolic response."
        },
        "hi": {
            "good_title": "एक अच्छा विकल्प",
            "mindful_title": "एक सचेत विकल्प",
            "good_summary": f"'{product_name}' का विश्लेषण करने के बाद, हमारा निष्कर्ष है कि यह एक पौष्टिक रूप से अच्छा विकल्प है जो संतुलित आहार में अच्छी तरह से फिट बैठता है।",
            "mindful_summary": f"'{product_name}' को स्कैन करने के लिए धन्यवाद। हमारा विश्लेषण बताता है कि इस भोजन का सेवन सीमित मात्रा में किया जाना चाहिए। चलिए विवरण देखें।",
            "gi_low": f"ग्लाइसेमिक इंडेक्स कम है, लगभग {gi:.0f}। यह बहुत अच्छा है, क्योंकि इसका मतलब है कि यह भोजन आपको स्थिर ऊर्जा प्रदान करेगा।",
            "gi_moderate": f"इस भोजन का ग्लाइसेमिक इंडेक्स मध्यम है, लगभग {gi:.0f}, जिसका अर्थ है कि स्थिर ऊर्जा सुनिश्चित करने के लिए इसे संतुलित भोजन के हिस्से के रूप में सेवन करना सबसे अच्छा है।",
            "gi_high": f"लगभग {gi:.0f} के उच्च ग्लाइसेमिक इंडेक्स के साथ, यह भोजन आपके रक्तप्रवाह में तेजी से चीनी छोड़ेगा। यह एक तेज ऊर्जा प्रदान कर सकता है, लेकिन बाद में गिरावट का कारण भी बन सकता है।",
            "nutri_good": f"न्यूट्री-स्कोर {nutri_score} है, जो एक शीर्ष-स्तरीय रेटिंग है। यह एक उच्च पोषण गुणवत्ता को इंगित करता है, जिससे यह एक बहुत ही स्वस्थ विकल्प बन जाता है।",
            "nutri_ok": f"{nutri_score} का न्यूट्री-स्कोर काफी अच्छा है। यह एक ठोस, रोजमर्रा की खाद्य वस्तु का प्रतिनिधित्व करता है।",
            "nutri_bad": f"न्यूट्री-स्कोर {nutri_score} है, जो बताता है कि यह कम पौष्टिक रूप से घना है, संभवतः चीनी, नमक, या संतृप्त वसा के उच्च स्तर के कारण।",
            "eco_good": f"स्थिरता के दृष्टिकोण से, यह आइटम लगभग {eco_score:.0f} के कम इको-स्कोर के साथ अच्छा स्कोर करता है, जो एक छोटे पर्यावरणीय पदचिह्न को इंगित करता है।",
            "eco_bad": f"इको-स्कोर लगभग {eco_score:.0f} है। यह एक मध्यम से उच्च पर्यावरणीय प्रभाव का सुझाव देता है, जो अक्सर इसके अवयवों से जुड़ा होता है।",
            "pro_tip_good": "अपने भोजन से अधिकतम लाभ प्राप्त करने के लिए, हमेशा भोजन को समग्र रूप से देखें। एक विविध रंगों और पोषक तत्वों वाली प्लेट बनाना एक स्वस्थ जीवन शैली की कुंजी है।",
            "pro_tip_bad": "यहाँ एक शक्तिशाली टिप है। उच्च ग्लाइसेमिक इंडेक्स का मुकाबला करने के लिए, इस भोजन को फाइबर के स्रोत, जैसे सब्जियों का साइड या मुट्ठी भर मेवे के साथ मिलाएं। यह चीनी के अवशोषण को धीमा करने और एक अधिक संतुलित चयापचय प्रतिक्रिया बनाने में मदद कर सकता है।"
        },
        "bn": {
            "good_title": "একটি ভাল পছন্দ",
            "mindful_title": "একটি সচেতন পছন্দ",
            "good_summary": f"'{product_name}' বিশ্লেষণ করার পর, আমাদের সিদ্ধান্ত হল যে এটি একটি পুষ্টিকরভাবে সঠিক বিকল্প যা একটি সুষম খাদ্যের মধ্যে ভালভাবে ফিট করে।",
            "mindful_summary": f"'{product_name}' স্ক্যান করার জন্য ধন্যবাদ। আমাদের বিশ্লেষণ অনুযায়ী, এই খাবারটি পরিমিতভাবে উপভোগ করা উচিত। চলুন বিস্তারিত জেনে নেওয়া যাক।",
            "gi_low": f"গ্লাইসেমিক ইনডেক্স কম, প্রায় {gi:.0f}। এটি চমৎকার, কারণ এর মানে হল এই খাবারটি আপনাকে স্থির এবং টেকসই শক্তি সরবরাহ করবে।",
            "gi_moderate": f"এই খাবারের গ্লাইসেমিক ইনডেক্স মাঝারি, প্রায় {gi:.0f}, যার মানে হল স্থির শক্তি নিশ্চিত করার জন্য এটি একটি সুষম খাবারের অংশ হিসাবে খাওয়া ভাল।",
            "gi_high": f"প্রায় {gi:.0f} এর উচ্চ গ্লাইসেমিক ইনডেক্স সহ, এই খাবারটি আপনার রক্তপ্রবাহে দ্রুত চিনি মুক্ত করবে। এটি দ্রুত শক্তি সরবরাহ করতে পারে, তবে পরে ক্লান্তির কারণও হতে পারে।",
            "nutri_good": f"নিউট্রিস্কোর হল {nutri_score}, যা একটি শীর্ষ-স্তরের রেটিং। এটি একটি উচ্চ পুষ্টির গুণমান নির্দেশ করে, যা এটিকে একটি খুব স্বাস্থ্যকর পছন্দ করে তোলে।",
            "nutri_ok": f"{nutri_score} এর নিউট্রিস্কোর বেশ ভাল। এটি একটি কঠিন, দৈনন্দিন খাদ্য আইটেমকে প্রতিনিধিত্ব করে।",
            "nutri_bad": f"নিউট্রিস্কোর হল {nutri_score}, যা থেকে বোঝা যায় যে এটি কম পুষ্টিকর, সম্ভবত চিনি, লবণ বা স্যাচুরেটেড ফ্যাট এর উচ্চ মাত্রার কারণে।",
            "eco_good": f"পরিবেশগত দৃষ্টিকোণ থেকে, এই আইটেমটি প্রায় {eco_score:.0f} এর কম ইকো-স্কোর দিয়ে ভাল স্কোর করে, যা একটি ছোট পরিবেশগত পদচিহ্ন নির্দেশ করে।",
            "eco_bad": f"ইকো-স্কোর প্রায় {eco_score:.0f}। এটি একটি মাঝারি থেকে উচ্চ পরিবেশগত প্রভাবের পরামর্শ দেয়, যা প্রায়শই এর উপাদানগুলির সাথে যুক্ত থাকে।",
            "pro_tip_good": "আপনার খাবার থেকে সর্বাধিক সুবিধা পেতে, সর্বদা খাবারটিকে সামগ্রিকভাবে বিবেচনা করুন। বিভিন্ন রঙ এবং পুষ্টির একটি প্লেট তৈরি করা একটি স্বাস্থ্যকর জীবনধারার চাবিকাঠি।",
            "pro_tip_bad": "এখানে একটি শক্তিশালী টিপ। উচ্চ গ্লাইসেমিক ইনডেক্স প্রতিরোধ করতে, এই খাবারটিকে ফাইবারের উৎসের সাথে যুক্ত করুন, যেমন সবজির একটি সাইড বা এক মুঠো বাদাম। এটি চিনির শোষণকে ধীর করতে এবং আরও ভারসাম্যপূর্ণ বিপাকীয় প্রতিক্রিয়া তৈরি করতে সহায়তা করতে পারে।"
        }
    }

    # Select the correct language, defaulting to English
    lang = text_templates.get(language, text_templates["en"])

    # --- Build Response using the selected language ---
    if data["ml_label"] == 'ok':
        response['title'] = lang["good_title"]
        response['summary'] = lang["good_summary"]
    else:
        response['title'] = lang["mindful_title"]
        response['summary'] = lang["mindful_summary"]

    score_insights = {}
    if gi < 55: score_insights['glycemicInsight'] = lang["gi_low"]
    elif gi <= 70: score_insights['glycemicInsight'] = lang["gi_moderate"]
    else: score_insights['glycemicInsight'] = lang["gi_high"]

    if nutri_score <= 1: score_insights['nutriScoreInsight'] = lang["nutri_good"]
    elif nutri_score <= 10: score_insights['nutriScoreInsight'] = lang["nutri_ok"]
    else: score_insights['nutriScoreInsight'] = lang["nutri_bad"]

    if eco_score < 40: score_insights['ecoScoreInsight'] = lang["eco_good"]
    else: score_insights['ecoScoreInsight'] = lang["eco_bad"]
    
    response['scoreInsights'] = score_insights

    if gi > 70 and nutri_score > 10:
        response['proTip'] = lang["pro_tip_bad"]
    else:
        response['proTip'] = lang["pro_tip_good"]
    
    return response

# --- The Main API Endpoint ---
@app.route('/analyze', methods=['POST'])
def analyze_product():
    try:
        data = request.get_json()
        
        required_fields = ['product_name', 'age', 'protein', 'carb', 'fat', 'sugar', 'fiber', 'language']
        if not all(field in data for field in required_fields):
            return jsonify({"error": "Missing required fields, including 'language'"}), 400

        # --- Perform all calculations ---
        calories = calculate_calories(data['protein'], data['carb'], data['fat'])
        nutri_score = calculate_nutri_score(data['protein'], data['fiber'], data['fat'], data['sugar'], calories)
        glycemic_index = min(100, max(0, np.random.normal(55, 15) + (data['sugar'] / 10) + (data['carb'] / 50)))
        eco_score = min(100, max(0, np.random.normal(50, 20) + (data['protein'] / 5)))
        
        age_group_str = f"{(data['age']//5)*5}-{(data['age']//5)*5+5}"
        if age_group_str not in age_encoder.classes_: 
            age_group_str = age_encoder.classes_[0]
            
        encoded_age = age_encoder.transform([age_group_str])[0]
        features = np.array([[data['protein'], data['carb'], data['fat'], data['sugar'], data['fiber'], encoded_age, glycemic_index, eco_score]])
        prediction = model.predict(features)[0]
        predicted_label = label_encoder.inverse_transform([prediction])[0]
        
        result_data = {
            "nutri_score": nutri_score, "glycemic_index": glycemic_index, 
            "eco_score": eco_score, "ml_label": predicted_label
        }
    
        # Generate the multilingual response and send it back to the app
        response = generate_multilingual_analysis(data['product_name'], result_data, data['language'])
        return jsonify(response)

    except Exception as e:
        return jsonify({"error": f"An internal error occurred: {str(e)}"}), 500

# --- Run the Server ---
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)