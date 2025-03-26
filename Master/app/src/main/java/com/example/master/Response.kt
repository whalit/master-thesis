package com.example.master
import android.content.Context
import org.json.JSONObject
import java.io.File


data class Response(val question: String, val answer: String)


object JsonHelper {
    // Function to save a response to a JSON file
    fun saveResponseToJson(response: Response, context: Context) {
        try {
            // Create a JSON object to store the response
            val jsonObject = JSONObject().apply {
                put("question", response.question)
                put("answer", response.answer)
            }

            // Convert JSON object to string format
            val jsonString = jsonObject.toString()

            // Get the internal storage directory (filesDir)
            val file = File(context.filesDir, "user_responses.json")

            // Write or append the response to the JSON file
            file.appendText("$jsonString\n") // Append each response as a new line
        } catch (e: Exception) {
            e.printStackTrace() // Log the error for debugging purposes
        }
    }

    fun readResponsesFromJson(context: Context): List<Response> {
        val file = File(context.filesDir, "user_responses.json")

        if (!file.exists()) {
            return emptyList() // Return empty list if the file doesn't exist
        }

        // Read the JSON file line by line and parse each response
        val responses = mutableListOf<Response>()
        file.forEachLine { line ->
            val jsonObject = JSONObject(line)
            val question = jsonObject.getString("question")
            val answer = jsonObject.getString("answer")
            responses.add(Response(question, answer))
        }

        return responses
    }
}
