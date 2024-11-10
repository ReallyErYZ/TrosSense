package org.cloudburstmc.protocol.bedrock.codec.v589.serializer;

import io.netty.buffer.ByteBuf;
import java.util.List;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.AvailableCommandsSerializer_v291$$ExternalSyntheticLambda14;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.AvailableCommandsSerializer_v291$$ExternalSyntheticLambda3;
import org.cloudburstmc.protocol.bedrock.codec.v575.serializer.UnlockedRecipesSerializer_v575;
import org.cloudburstmc.protocol.bedrock.packet.UnlockedRecipesPacket;

/* loaded from: classes5.dex */
public class UnlockedRecipesSerializer_v589 extends UnlockedRecipesSerializer_v575 {
    protected static final UnlockedRecipesPacket.ActionType[] ACTIONS = UnlockedRecipesPacket.ActionType.values();

    @Override // org.cloudburstmc.protocol.bedrock.codec.v575.serializer.UnlockedRecipesSerializer_v575, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, UnlockedRecipesPacket packet) {
        buffer.writeIntLE(packet.getAction().ordinal());
        List<String> unlockedRecipes = packet.getUnlockedRecipes();
        helper.getClass();
        helper.writeArray(buffer, unlockedRecipes, new AvailableCommandsSerializer_v291$$ExternalSyntheticLambda14(helper));
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.v575.serializer.UnlockedRecipesSerializer_v575, org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, UnlockedRecipesPacket packet) {
        packet.setAction(ACTIONS[buffer.readIntLE()]);
        List<String> unlockedRecipes = packet.getUnlockedRecipes();
        helper.getClass();
        helper.readArray(buffer, unlockedRecipes, new AvailableCommandsSerializer_v291$$ExternalSyntheticLambda3(helper));
    }
}