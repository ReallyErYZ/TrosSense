package org.cloudburstmc.protocol.bedrock.codec.v503.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v440.serializer.RemoveVolumeEntitySerializer_v440;
import org.cloudburstmc.protocol.bedrock.packet.RemoveVolumeEntityPacket;
import org.cloudburstmc.protocol.common.util.VarInts;

/* loaded from: classes5.dex */
public class RemoveVolumeEntitySerializer_v503 extends RemoveVolumeEntitySerializer_v440 {
    @Override // org.cloudburstmc.protocol.bedrock.codec.v440.serializer.RemoveVolumeEntitySerializer_v440, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, RemoveVolumeEntityPacket packet) {
        super.serialize(buffer, helper, packet);
        VarInts.writeInt(buffer, packet.getDimension());
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.v440.serializer.RemoveVolumeEntitySerializer_v440, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, RemoveVolumeEntityPacket packet) {
        super.deserialize(buffer, helper, packet);
        packet.setDimension(VarInts.readInt(buffer));
    }
}
