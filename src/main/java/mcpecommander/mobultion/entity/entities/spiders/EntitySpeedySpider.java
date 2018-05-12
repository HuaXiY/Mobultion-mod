package mcpecommander.mobultion.entity.entities.spiders;

import javax.annotation.Nullable;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;

import mcpecommander.mobultion.Reference;
import mcpecommander.mobultion.entity.animation.AnimationLookAt;
import mcpecommander.mobultion.entity.entityAI.spidersAI.EntityAISpiderAttack;
import mcpecommander.mobultion.entity.entityAI.spidersAI.EntityAISpiderTarget;
import mcpecommander.mobultion.mobConfigs.SpidersConfig;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySpeedySpider extends EntityAnimatedSpider{
	
	protected static AnimationHandler animHandler = CraftStudioApi.getNewAnimationHandler(EntitySpeedySpider.class);
	
	static{
		EntitySpeedySpider.animHandler.addAnim(Reference.MOD_ID, "speedy_pull", "speedy_spider", false);
		EntitySpeedySpider.animHandler.addAnim(Reference.MOD_ID, "lookat", new AnimationLookAt("Head"));
	}
	
	public EntitySpeedySpider(World worldIn) {
		super(worldIn);
		this.setSize(1.4f, 0.9f);
		this.stepHeight = 1.0f;
	}
	
	@Override
	public <T extends IAnimated> AnimationHandler<T> getAnimationHandler() {
		return EntitySpeedySpider.animHandler;
	}

	@Override
    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAISpiderAttack(this));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAISpiderTarget(this, EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAISpiderTarget(this, EntityIronGolem.class));
    }
    
    @Override
    public double getMountedYOffset()
    {
        return this.height * 0.6F;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(SpidersConfig.spiders.speedy.speed);
    } 
    
    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
		livingdata = super.onInitialSpawn(difficulty, livingdata);

		if (this.world.rand.nextInt(SpidersConfig.spiders.speedy.jockeyChance) == 0 && !this.world.isRemote) {
			EntitySkeleton entityskeleton = new EntitySkeleton(this.world);
			entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
			entityskeleton.onInitialSpawn(difficulty, (IEntityLivingData) null);
			this.world.spawnEntity(entityskeleton);
			entityskeleton.startRiding(this);
		}

		return livingdata;
	}
    
    @Override
    public float getEyeHeight()
    {
        return 0.65F;
    }
    
    @Override
    public void onLivingUpdate() {
    	super.onLivingUpdate();
    	if(this.isWorldRemote()){
    		if(!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "lookat", this)){
    			this.getAnimationHandler().startAnimation(Reference.MOD_ID, "lookat", 0, this);
    		}

    		if(!this.getAnimationHandler().isHoldAnimationActive(Reference.MOD_ID + ":speedy_pull", this) && this.isMoving()){
    			this.getAnimationHandler().startAnimation(Reference.MOD_ID, "speedy_pull", 0, this);
    		}
    		
    		if(this.getAnimationHandler().isHoldAnimationActive(Reference.MOD_ID + ":speedy_pull", this) && !this.isMoving()){
    			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "speedy_pull", this);
    		}
    	}
    }

}


