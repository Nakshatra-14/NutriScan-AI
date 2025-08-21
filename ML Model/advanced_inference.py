import joblib
import numpy as np
import random

# --- Load the Trained Model and Encoders (should be done once when the app starts) ---
model = joblib.load("model_v2.joblib")
age_encoder = joblib.load("age_encoder_v2.joblib")
label_encoder = joblib.load("label_encoder_v2.joblib")

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

# --- THE CORE AI BRAIN for Display & TTS ---
def generate_professional_analysis(product_name, data):
    """
    Crafts the final, professional, dual-use response for both
    app display and a Text-to-Speech engine, now including all scores.
    """
    response = {}

    # --- Part 1: Title & Summary ---
    if data["ml_label"] == 'ok':
        response['title'] = "A Good Choice"
        response['summary'] = f"After analyzing '{product_name}', our conclusion is that it's a nutritionally sound option that fits well within a balanced diet."
    else:
        response['title'] = "A Mindful Choice"
        response['summary'] = f"Thank you for scanning '{product_name}'. Our analysis suggests this is a food to be enjoyed in moderation. Let's explore the details."

    # --- Part 2: Score Insights (NEW) ---
    score_insights = {}
    
    # Dynamic Glycemic Index (GI) Insight
    gi = data['glycemic_index']
    if gi < 55:
        score_insights['glycemicInsight'] = f"The Glycemic Index is low, around {gi:.0f}. This is excellent, as it means this food will likely provide you with steady, sustained energy."
    elif gi <= 70:
        score_insights['glycemicInsight'] = f"This food has a moderate Glycemic Index of about {gi:.0f}, meaning it's best consumed as part of a balanced meal to ensure stable energy."
    else:
        score_insights['glycemicInsight'] = f"With a high Glycemic Index of approximately {gi:.0f}, this food will release sugar into your bloodstream quickly. This can provide a fast energy boost, but may also lead to a crash later on."

    # Dynamic Nutri-Score Insight
    nutri_score = data['nutri_score']
    if nutri_score <= 1:
        score_insights['nutriScoreInsight'] = f"The Nutri-Score is {nutri_score}, which is a top-tier rating. This indicates a high nutritional quality, making it a very healthy choice."
    elif nutri_score <= 10:
         score_insights['nutriScoreInsight'] = f"A Nutri-Score of {nutri_score} is quite good. It represents a solid, everyday food item that contributes positively to your diet."
    else:
        score_insights['nutriScoreInsight'] = f"The Nutri-Score is {nutri_score}, which suggests it's less nutritionally dense, likely due to higher levels of sugar, salt, or saturated fat."

    # Dynamic Eco-Score Insight
    eco_score = data['eco_score']
    if eco_score < 40:
        score_insights['ecoScoreInsight'] = f"From a sustainability perspective, this item scores well with a low Eco-Score of about {eco_score:.0f}. This indicates a smaller environmental footprint."
    else:
        score_insights['ecoScoreInsight'] = f"The Eco-Score is around {eco_score:.0f}. This suggests a moderate to high environmental impact, which is often associated with its ingredients or production process."
    
    response['scoreInsights'] = score_insights
    
    # --- Part 3: The Pro-Tip ---
    pro_tip_text = ""
    # This logic can now be even smarter by combining scores
    if gi > 70 and nutri_score > 10:
        pro_tip_text = "Here is a powerful tip. To counteract the high Glycemic Index, pair this food with a source of fiber, like a side of vegetables or a handful of nuts. This can help slow down the sugar absorption and create a more balanced metabolic response."
    else:
        pro_tip_text = "To get the most from your food, always consider the meal as a whole. A single item is just one part of the story; building a plate with a variety of colors and nutrients is key to a healthy lifestyle."
    response['proTip'] = pro_tip_text
    
    return response

# --- The Main Backend Function ---
def get_professional_food_analysis(product_name, age, protein, carb, fat, sugar, fiber):
    # ... (same calculation logic as before)
    calories = calculate_calories(protein, carb, fat)
    nutri_score = calculate_nutri_score(protein, fiber, fat, sugar, calories)
    glycemic_index = min(100, max(0, np.random.normal(55, 15) + (sugar / 10) + (carb / 50)))
    eco_score = min(100, max(0, np.random.normal(50, 20) + (protein / 5)))
    age_group_str = f"{(age//5)*5}-{(age//5)*5+5}"
    if age_group_str not in age_encoder.classes_: age_group_str = age_encoder.classes_[0]
    encoded_age = age_encoder.transform([age_group_str])[0]
    features = np.array([[protein, carb, fat, sugar, fiber, encoded_age, glycemic_index, eco_score]])
    prediction = model.predict(features)[0]
    predicted_label = label_encoder.inverse_transform([prediction])[0]
    result_data = {"calories": calories, "nutri_score": nutri_score, "glycemic_index": glycemic_index, "eco_score": eco_score, "ml_label": predicted_label}
    
    return generate_professional_analysis(product_name, result_data)

# --- Example of how your backend would use this ---
if __name__ == "__main__":
    
    # User scans a "Double Cheeseburger"
    analysis = get_professional_food_analysis(
        product_name="Double Cheeseburger", age=23, 
        protein=55, carb=240, fat=70, sugar=45, fiber=20
    )
    
    # --- FOR APP DISPLAY ---
    print("--- 📱 App Display Version ---")
    print(f"## {analysis['title']}")
    print(f"{analysis['summary']}")
    print("\n### Score Insights")
    print(f"**Glycemic Index:** {analysis['scoreInsights']['glycemicInsight']}")
    print(f"**Nutri-Score:** {analysis['scoreInsights']['nutriScoreInsight']}")
    print(f"**Eco-Score:** {analysis['scoreInsights']['ecoScoreInsight']}")
    print(f"\n### Pro-Tip")
    print(f"{analysis['proTip']}")
        
    print("\n" + "="*50 + "\n")
    
    # --- FOR TEXT-TO-SPEECH ---
    print("--- 🔊 TTS Narrative Version ---")
    tts_string = " ".join(filter(None, [
        analysis['summary'], 
        analysis['scoreInsights']['glycemicInsight'],
        analysis['scoreInsights']['nutriScoreInsight'],
        analysis['scoreInsights']['ecoScoreInsight'],
        analysis['proTip']
    ]))
    print(tts_string)