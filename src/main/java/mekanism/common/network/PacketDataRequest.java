package mekanism.common.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Supplier;
import mekanism.api.Coord4D;
import mekanism.api.transmitters.IGridTransmitter;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.base.ITileNetwork;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityMultiblock;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketDataRequest {

    private Coord4D coord4D;

    public PacketDataRequest(Coord4D coord) {
        coord4D = coord;
    }

    public static void handle(PacketDataRequest message, Supplier<Context> context) {
        PlayerEntity player = PacketHandler.getPlayer(context);
        PacketHandler.handlePacket(() -> {
            World worldServer = DimensionManager.getWorld(message.coord4D.dimension);
            if (worldServer != null) {
                TileEntity tileEntity = message.coord4D.getTileEntity(worldServer);
                if (tileEntity instanceof TileEntityMultiblock) {
                    ((TileEntityMultiblock<?>) tileEntity).sendStructure = true;
                }
                if (CapabilityUtils.hasCapability(tileEntity, Capabilities.GRID_TRANSMITTER_CAPABILITY, null)) {
                    IGridTransmitter transmitter = CapabilityUtils.getCapability(tileEntity, Capabilities.GRID_TRANSMITTER_CAPABILITY, null);
                    transmitter.setRequestsUpdate();
                    if (transmitter.hasTransmitterNetwork()) {
                        transmitter.getTransmitterNetwork().addUpdate(player);
                    }
                }
                if (CapabilityUtils.hasCapability(tileEntity, Capabilities.TILE_NETWORK_CAPABILITY, null)) {
                    ITileNetwork network = CapabilityUtils.getCapability(tileEntity, Capabilities.TILE_NETWORK_CAPABILITY, null);
                    Mekanism.packetHandler.sendTo(new PacketTileEntity(tileEntity, network.getNetworkedData()), (ServerPlayerEntity) player);
                }
            }
        }, player);
    }

    public static void encode(PacketDataRequest pkt, PacketBuffer buf) {
        pkt.coord4D.write(buf);
    }

    public static PacketDataRequest decode(PacketBuffer buf) {
        return new PacketDataRequest(Coord4D.read(buf));
    }
}