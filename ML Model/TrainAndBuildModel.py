import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score, classification_report
import joblib

# 1. Load the Dataset
df = pd.read_csv("food_dataset_v2.csv")

# 2. Preprocessing and Encoding
# Separate features (X) and the target label (y)
X = df.drop("label", axis=1)
y = df["label"]

# We need to encode the 'age_group' column from text to numbers
age_encoder = LabelEncoder()
X["age_group"] = age_encoder.fit_transform(X["age_group"])

# We also encode the target labels ('bad', 'ok')
label_encoder = LabelEncoder()
y_encoded = label_encoder.fit_transform(y)

# 3. Split Data into Training and Testing Sets
# This helps us see how well our model performs on new, unseen data
X_train, X_test, y_train, y_test = train_test_split(
    X, y_encoded, test_size=0.2, random_state=42, stratify=y_encoded
)

# 4. Train the RandomForest Model
# We use 100 "trees" in our forest and a random_state for reproducibility
model = RandomForestClassifier(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# 5. Evaluate the Model's Performance
y_pred = model.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
print(f"Model Accuracy: {accuracy * 100:.2f}%")
print("\nClassification Report:")
# We use the encoder to see the original labels ('bad', 'ok') in the report
print(classification_report(y_test, y_pred, target_names=label_encoder.classes_))

# 6. Save the Model and Encoders
# We'll need these files for our final prediction function
joblib.dump(model, "model_v2.joblib")
joblib.dump(age_encoder, "age_encoder_v2.joblib")
joblib.dump(label_encoder, "label_encoder_v2.joblib")

print("\nModel and encoders saved successfully!")
print("- model_v2.joblib")
print("- age_encoder_v2.joblib")
print("- label_encoder_v2.joblib")