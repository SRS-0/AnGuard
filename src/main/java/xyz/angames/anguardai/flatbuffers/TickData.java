package xyz.angames.anguardai.flatbuffers;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class TickData extends Table {

    public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
    public static TickData getRootAsTickData(ByteBuffer _bb) { return getRootAsTickData(_bb, new TickData()); }
    public static TickData getRootAsTickData(ByteBuffer _bb, TickData obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
    public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
    public TickData __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }
    public float deltaYaw() { int o = __offset(4); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float deltaPitch() { int o = __offset(6); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float accelYaw() { int o = __offset(8); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float accelPitch() { int o = __offset(10); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float jerkPitch() { int o = __offset(12); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float jerkYaw() { int o = __offset(14); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float gcdErrorYaw() { int o = __offset(16); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }
    public float gcdErrorPitch() { int o = __offset(18); return o != 0 ? bb.getFloat(o + bb_pos) : 0.0f; }

    public static int createTickData(FlatBufferBuilder builder, float deltaYaw, float deltaPitch, float accelYaw, float accelPitch, float jerkPitch, float jerkYaw, float gcdErrorYaw, float gcdErrorPitch) {
        builder.startTable(8);
        TickData.addGcdErrorPitch(builder, gcdErrorPitch);
        TickData.addGcdErrorYaw(builder, gcdErrorYaw);
        TickData.addJerkYaw(builder, jerkYaw);
        TickData.addJerkPitch(builder, jerkPitch);
        TickData.addAccelPitch(builder, accelPitch);
        TickData.addAccelYaw(builder, accelYaw);
        TickData.addDeltaPitch(builder, deltaPitch);
        TickData.addDeltaYaw(builder, deltaYaw);
        return TickData.endTickData(builder);
    }
    public static void startTickData(FlatBufferBuilder builder) { builder.startTable(8); }
    public static void addDeltaYaw(FlatBufferBuilder builder, float deltaYaw) { builder.addFloat(0, deltaYaw, 0.0f); }
    public static void addDeltaPitch(FlatBufferBuilder builder, float deltaPitch) { builder.addFloat(1, deltaPitch, 0.0f); }
    public static void addAccelYaw(FlatBufferBuilder builder, float accelYaw) { builder.addFloat(2, accelYaw, 0.0f); }
    public static void addAccelPitch(FlatBufferBuilder builder, float accelPitch) { builder.addFloat(3, accelPitch, 0.0f); }
    public static void addJerkPitch(FlatBufferBuilder builder, float jerkPitch) { builder.addFloat(4, jerkPitch, 0.0f); }
    public static void addJerkYaw(FlatBufferBuilder builder, float jerkYaw) { builder.addFloat(5, jerkYaw, 0.0f); }
    public static void addGcdErrorYaw(FlatBufferBuilder builder, float gcdErrorYaw) { builder.addFloat(6, gcdErrorYaw, 0.0f); }
    public static void addGcdErrorPitch(FlatBufferBuilder builder, float gcdErrorPitch) { builder.addFloat(7, gcdErrorPitch, 0.0f); }
    public static int endTickData(FlatBufferBuilder builder) { int o = builder.endTable(); return o; }

    public static final class Vector extends BaseVector {
        public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }
        public TickData get(int j) { return get(new TickData(), j); }
        public TickData get(TickData obj, int j) { return obj.__assign(__indirect(__element(j), bb), bb); }
    }
}