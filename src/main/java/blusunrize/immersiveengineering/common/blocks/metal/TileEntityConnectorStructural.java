package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.google.common.base.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class TileEntityConnectorStructural extends TileEntityConnectorLV implements IHammerInteraction, IOBJModelCallback<IBlockState>
{
	public float rotation = 0;

	@Override
	protected boolean canTakeMV()
	{
		return false;
	}
	@Override
	protected boolean canTakeLV()
	{
		return false;
	}
//	@Override
//	public boolean canUpdate()
//	{
//		return false;
//	}

	@Override
	public boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ)
	{
		rotation += player.isSneaking()?-22.5f:22.5f;
		rotation %= 360;
		markDirty();
		worldObj.addBlockEvent(getPos(), this.getBlockType(), 254, 0);
		return true;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		nbt.setFloat("rotation", rotation);
	}
	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		rotation = nbt.getFloat("rotation");
		if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT && worldObj!=null)
			this.markContainingBlockForUpdate(null);
	}

	@Override
	public Vec3d getConnectionOffset(Connection con)
	{
		EnumFacing side = facing.getOpposite();
		double conRadius = .03125;
		return new Vec3d(.5+side.getFrontOffsetX()*(-.125-conRadius), .5+side.getFrontOffsetY()*(-.125-conRadius), .5+side.getFrontOffsetZ()*(-.125-conRadius));
	}

	@Override
	int getRenderRadiusIncrease()
	{
		return WireType.STRUCTURE_STEEL.getMaxLength();
	}

	@Override
	public int getMaxInput()
	{
		return 0;
	}
	@Override
	public int getMaxOutput()
	{
		return 0;
	}

	@Override
	public boolean canConnectCable(WireType cableType, TargetingInfo target)
	{
		if(cableType!=WireType.STRUCTURE_ROPE && cableType!=WireType.STRUCTURE_STEEL)
			return false;
		return limitType==null||limitType==cableType;
	}
	@Override
	public boolean isEnergyOutput()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getTextureReplacement(IBlockState object, String material)
	{
		return null;
	}
	@Override
	public boolean shouldRenderGroup(IBlockState object, String group)
	{
		return true;
	}
	@Override
	public Optional<TRSRTransformation> applyTransformations(IBlockState object, String group, Optional<TRSRTransformation> transform)
	{
		Matrix4 mat = transform.isPresent()?new Matrix4(transform.get().getMatrix()):new Matrix4();
		mat = mat.translate(.5,0,.5).rotate(Math.toRadians(rotation),0,1,0).translate(-.5,0,-.5);
		transform = Optional.of( new TRSRTransformation(mat.toMatrix4f()));
		return transform;
	}
	@Override
	public Matrix4 handlePerspective(IBlockState Object, TransformType cameraTransformType, Matrix4 perspective)
	{
		return perspective;
	}
	
	@Override
	public String getCacheKey(IBlockState object)
	{
		return Float.toString(rotation);
	}

//	@Override
//	public boolean canConnectEnergy(ForgeDirection from)
//	{
//		return false;
//	}
//	@Override
//	public int receiveEnergy(ForgeDirection from, int maxReceive,boolean simulate)
//	{
//		return 0;
//	}
//	@Override
//	public int getEnergyStored(ForgeDirection from)
//	{
//		return 0;
//	}
//	@Override
//	public int getMaxEnergyStored(ForgeDirection from)
//	{
//		return 0;
//	}
}