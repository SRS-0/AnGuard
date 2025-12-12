package xyz.angames.anguardai.flatbuffers;

import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class TickDataSequence extends Table {
    public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
    public static TickDataSequence getRootAsTickDataSequence(ByteBuffer _bb) { return getRootAsTickDataSequence(_bb, new TickDataSequence()); }
    public static TickDataSequence getRootAsTickDataSequence(ByteBuffer _bb, TickDataSequence obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
    public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
    public TickDataSequence __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }
    public TickData ticks(int j) { return ticks(new TickData(), j); }
    public TickData ticks(TickData obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
    public int ticksLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

    public static int createTickDataSequence(FlatBufferBuilder builder, int ticksOffset) {
        builder.startTable(1);
        TickDataSequence.addTicks(builder, ticksOffset);
        return TickDataSequence.endTickDataSequence(builder);
    }
    public static void startTickDataSequence(FlatBufferBuilder builder) { builder.startTable(1); }
    public static void addTicks(FlatBufferBuilder builder, int ticksOffset) { builder.addOffset(0, ticksOffset, 0); }
    public static int createTicksVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);
        for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]);
        return builder.endVector();
    }
    public static int endTickDataSequence(FlatBufferBuilder builder) { int o = builder.endTable(); return o; }
    public static void finishTickDataSequenceBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
}