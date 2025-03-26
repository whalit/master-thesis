import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteModel(context: Context) {

    // Load the TFLite model from assets
    private var interpreter: Interpreter

    init {
        val assetFileDescriptor = context.assets.openFd("test_model.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val model = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(model.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(model)

        interpreter = Interpreter(buffer)
    }

    // Create a method to run inference (adjust input/output types accordingly)
    fun predict(inputData: FloatArray): FloatArray {
        val outputData = FloatArray(1) // Assuming the model outputs one float value
        interpreter.run(inputData, outputData)
        return outputData
    }

    // Close the interpreter when you're done with it
    fun close() {
        interpreter.close()
    }
}
