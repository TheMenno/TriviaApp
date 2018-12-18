package com.example.menno_000.triviaapp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionsRequest implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Context context;
    private Callback callback;


    // A callback to the main activity
    public interface Callback {
        void gotQuestions(ArrayList<ArrayList> questions);
        void gotQuestionsError(String message);
    }


    // Constructor
    public QuestionsRequest(Context context) {
        this.context = context;
    }


    // Connect to the api
    public void getQuestions(Callback call) {

        // Set up a queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create the data request
        String url ="https://opentdb.com/api.php?amount=30&category=15&type=multiple";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                this, this);

        // Add the request to the queue
        queue.add(jsonObjectRequest);

        callback = call;
    }


    // Acts when API can't be accessed
    @Override
    public void onErrorResponse(VolleyError error) {
        callback.gotQuestionsError(error.getMessage());
    }


    // Acts when API can be accessed
    @Override
    public void onResponse(JSONObject response) {
        try {
            // Open the results
            JSONArray raw_JSON = response.getJSONArray("results");

            // Set up a container
            ArrayList<String> raw_Strings = new ArrayList<>();

            // Separate the questions
            for (int i = 0; i < raw_JSON.length(); i++) {
                String question = raw_JSON.get(i).toString();
                question = question.replace("&quot;", "'");
                question = question.replace("&#039;", "'");
                question = question.replace("&eacute;", "é");

                raw_Strings.add(question);
            }

            // Set up final container
            ArrayList<ArrayList> everything_list = new ArrayList<>();

            // Iterate through all questions and their other information
            for (int i = 0; i < raw_Strings.size(); i++) {

                // Get and unpack the data
                String everything = raw_Strings.get(i);
                List<String> splitList = (Arrays.asList(everything.split(",")));

                // Set up temporary container
                ArrayList<String> questions_list = new ArrayList<>();

                // Initialise variables
                String value = "";
                String category = "";
                String current_case = "";

                // Iterate through all information of one question
                for (int j = 0; j < splitList.size(); j++) {

                    // Get a question
                    String question = splitList.get(j);

                    // Remove fluff
                    if (j == 0) {
                        question = question.substring(1);
                    } else if (j == splitList.size() - 1) {
                        question = question.substring(0, question.length() - 2);
                    }

                    // Split the keys and values
                    List<String> dataList = (Arrays.asList(question.split(":", 2)));

                    // Check if there is both a key and a value
                    if (dataList.size() == 2) {

                        // Find the key and value
                        category = cutEdges(dataList.get(0));
                        value = cutEdges(dataList.get(1));

                        // Remove fluff
                        if (j == splitList.size() - 3) {
                            value = value.substring(1);
                        }

                        // Add the right values to the container
                        switch (category) {
                            case "difficulty":
                                current_case = "difficulty";
                                questions_list.add(value);
                                break;
                            case "question":
                                current_case = "question";
                                questions_list.add(value);
                                break;
                            case "correct_answer":
                                current_case = "correct_answer";
                                questions_list.add(value);
                                break;
                            case "incorrect_answers":
                                current_case = "incorrect_answers";
                                questions_list.add(value);
                                break;
                        }
                    }
                    // There was only a value, something went wrong with parsing
                    else {
                        value = dataList.get(0);
                        String last_entry = questions_list.get(questions_list.size() - 1);

                        if (current_case.equals("incorrect_answers")) {
                            questions_list.add(cutEdges(value));
                        } else {
                            last_entry = last_entry + " " + value;
                            questions_list.remove(questions_list.size() - 1);
                            questions_list.add(last_entry);
                        }
                    }
                }

                // Add the question to the final list
                everything_list.add(questions_list);
            }

            callback.gotQuestions(everything_list);

        } catch (JSONException e) {
            // Error message
            e.printStackTrace();
            callback.gotQuestionsError("JSONException");
        }
    }

    public String cutEdges(String string) {
        if (string.length() > 1) {

            string = string.substring(1);
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }
}
