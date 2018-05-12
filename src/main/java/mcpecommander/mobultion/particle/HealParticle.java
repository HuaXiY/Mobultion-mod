package mcpecommander.mobultion.particle;

import mcpecommander.mobultion.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HealParticle extends Particle{
	private final ResourceLocation lava = new ResourceLocation(Reference.MOD_ID ,"particle/heal");
	private AxisAlignedBB finalTarget;

	public HealParticle(World worldIn, double posXIn, double posYIn, double posZIn, float red, Vec3d vec3, AxisAlignedBB pos) {
		super(worldIn, posXIn, posYIn, posZIn);
		
		particleGravity = Blocks.BONE_BLOCK.blockParticleGravity;
		this.particleAlpha = 0.95f;
		
		this.motionX = -vec3.x/4;
		this.motionY = -vec3.y/4;
		this.motionZ = -vec3.z/4;
		this.particleScale *= this.rand.nextFloat() * 2.0F + 0.5F;
	    this.particleMaxAge = 80;
	    this.setRBGColorF(red, 1.0f, 1.0f);
	    this.finalTarget = pos;
	    
	    TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(lava.toString());
	    setParticleTexture(sprite);
	}
	
	@Override
	public int getBrightnessForRender(float p_189214_1_)
    {
        int i = super.getBrightnessForRender(p_189214_1_);
        int j = 240;
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	  {
	    double minU = this.particleTexture.getMinU();
	    double maxU = this.particleTexture.getMaxU();
	    double minV = this.particleTexture.getMinV();
	    double maxV = this.particleTexture.getMaxV();

	    double scale = 0.1F * this.particleScale;
	    final double scaleLR = scale;
	    final double scaleUD = scale;
	    double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
	    double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
	    double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
	    int combinedBrightness = this.getBrightnessForRender(partialTicks);
	    int skyLightTimes16 = combinedBrightness >> 16 & 65535;
	    int blockLightTimes16 = combinedBrightness & 65535;

	    buffer.pos(x - rotationX * scaleLR - rotationXY * scaleUD,
	            y - rotationZ * scaleUD,
	            z - rotationYZ * scaleLR - rotationXZ * scaleUD)
	                 .tex(maxU, maxV)
	                 .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
	                 .lightmap(skyLightTimes16, blockLightTimes16)
	                 .endVertex();
	    buffer.pos(x - rotationX * scaleLR + rotationXY * scaleUD,
	            y + rotationZ * scaleUD,
	            z - rotationYZ * scaleLR + rotationXZ * scaleUD)
	            .tex(maxU, minV)
	            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
	            .lightmap(skyLightTimes16, blockLightTimes16)
	            .endVertex();
	    buffer.pos(x + rotationX * scaleLR + rotationXY * scaleUD,
	            y + rotationZ * scaleUD,
	            z + rotationYZ * scaleLR + rotationXZ * scaleUD)
	            .tex(minU, minV)
	            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
	            .lightmap(skyLightTimes16, blockLightTimes16)
	            .endVertex();
	    buffer.pos(x + rotationX * scaleLR - rotationXY * scaleUD,
	            y - rotationZ * scaleUD,
	            z + rotationYZ * scaleLR - rotationXZ * scaleUD)
	            .tex(minU, maxV)
	            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
	            .lightmap(skyLightTimes16, blockLightTimes16)
	            .endVertex();

	  }
	
	@Override
	public int getFXLayer() {
		return 1;
	}
	
	@Override
	public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        move(motionX, motionY, motionZ);

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        float f = (float)this.particleAge / (float)this.particleMaxAge;
//        
//        System.out.println(finalTarget.distanceSq(this.posX, this.posY, this.posZ));
        if(this.getBoundingBox().intersects(finalTarget)){
        	this.setExpired();
        	
        }

    }

}
