package com.example.littlebylittle;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class MindfulnessXmlParser {

    private static final String TAG = "MindfulnessXmlParser";

    public static class PromptItem {
        private final String category;
        private final String title;
        private final String question;
        private final String affirmation;

        public PromptItem(String category, String title, String question, String affirmation) {
            this.category = category;
            this.title = title;
            this.question = question;
            this.affirmation = affirmation;
        }

        public String getCategory() {
            return category;
        }

        public String getTitle() {
            return title;
        }

        public String getQuestion() {
            return question;
        }

        public String getAffirmation() {
            return affirmation;
        }
    }

    public static List<PromptItem> parsePrompts(Context context) {
        List<PromptItem> prompts = new ArrayList<>();

        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.mindfulness_prompts);
            int eventType = parser.getEventType();

            String currentCategory = "";
            String currentTitle = "";
            String currentQuestion = "";
            String currentAffirmation = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("prompt".equalsIgnoreCase(tagName)) {
                            currentCategory = parser.getAttributeValue(null, "category");
                        } else if ("title".equalsIgnoreCase(tagName)) {
                            currentTitle = parser.nextText();
                        } else if ("question".equalsIgnoreCase(tagName)) {
                            currentQuestion = parser.nextText();
                        } else if ("affirmation".equalsIgnoreCase(tagName)) {
                            currentAffirmation = parser.nextText();
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("prompt".equalsIgnoreCase(tagName)) {
                            prompts.add(new PromptItem(currentCategory, currentTitle, currentQuestion, currentAffirmation));
                        }
                        break;
                }

                eventType = parser.next();
            }

            Log.d(TAG, "Successfully parsed " + prompts.size() + " prompts from XML");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing mindfulness prompts XML", e);
        }

        return prompts;
    }
}
