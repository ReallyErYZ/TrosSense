package org.cloudburstmc.protocol.bedrock.codec.v313.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.ResourcePackStackSerializer_v291;
import org.cloudburstmc.protocol.bedrock.data.ExperimentData;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

/* loaded from: classes5.dex */
public class ResourcePackStackSerializer_v313 extends ResourcePackStackSerializer_v291 {
    public static final ResourcePackStackSerializer_v313 INSTANCE = new ResourcePackStackSerializer_v313();
    private static final ExperimentData LEGACY_EXPERIMENT_DATA = new ExperimentData("legacy_experiment", true);

    @Override // org.cloudburstmc.protocol.bedrock.codec.v291.serializer.ResourcePackStackSerializer_v291, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, ResourcePackStackPacket packet) {
        super.serialize(buffer, helper, packet);
        buffer.writeBoolean(packet.getExperiments().contains(LEGACY_EXPERIMENT_DATA));
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.v291.serializer.ResourcePackStackSerializer_v291, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, ResourcePackStackPacket packet) {
        super.deserialize(buffer, helper, packet);
        if (buffer.readBoolean()) {
            packet.getExperiments().add(LEGACY_EXPERIMENT_DATA);
        }
    }
}
