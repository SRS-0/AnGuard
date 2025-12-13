🛡️ AnGuard AI

Neural Network AntiCheat for Minecraft 1.21+
AnGuard is a revolutionary AntiCheat system that offloads 100% of the heavy Artificial Intelligence processing to a remote server. Unlike traditional AI plugins that consume server CPU and lower TPS, AnGuard acts as a lightweight data collector, streaming movement data to a dedicated API server backend for analysis.

Architecture:
    Collection: The Java plugin captures raw packet data (Rotation, Acceleration, Sensitivity/GCD) using ProtocolLib.
    Transmission: Movement history (40-tick sequences) is streamed via HTTP/2 to a remote API.
    Inference: An ONNX LSTM (Long Short-Term Memory) model analyzes the behavioral pattern.
    Verdict: The API server returns a confidence score. The plugin applies a Smart Buffer or Damage Reduction based on the result.
