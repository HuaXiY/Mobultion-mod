package mcpecommander.mobultion.entity.entities.skeletons;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;

import mcpecommander.mobultion.Reference;
import mcpecommander.mobultion.entity.animation.AnimationLookAt;
import mcpecommander.mobultion.entity.animation.AnimationRiding;
import mcpecommander.mobultion.entity.entityAI.skeletonsAI.EntityAIAttackRangedModBow;
import mcpecommander.mobultion.entity.entityAI.skeletonsAI.EntityAIForestSkeletonMoveToTree;
import mcpecommander.mobultion.init.ModItems;
import mcpecommander.mobultion.init.ModSounds;
import mcpecommander.mobultion.mobConfigs.SkeletonsConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySniperSkeleton extends EntityAnimatedSkeleton{
	
	protected static AnimationHandler animHandler = CraftStudioApi.getNewAnimationHandler(EntitySniperSkeleton.class);
	private final EntityAIAttackRangedModBow<EntitySniperSkeleton> aiArrowAttack = new EntityAIAttackRangedModBow<EntitySniperSkeleton>(this, 1.0D, 20, 15.0F);
    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false)
    {

        @Override
		public void resetTask()
        {
            super.resetTask();
            EntitySniperSkeleton.this.setSwingingArms(false);
        }

        @Override
		public void startExecuting()
        {
            super.startExecuting();
            EntitySniperSkeleton.this.setSwingingArms(true);
        }
    };
	boolean flag = false;

    static {
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_death", "forest_skeleton", false);
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_walk", "forest_skeleton", true);
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_walk_hands", "forest_skeleton", true);
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_holding_bow", "forest_skeleton", true);
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "lookat", new AnimationLookAt("Head"));
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "riding", new AnimationRiding());
    	EntitySniperSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_spawn", "skeleton_death");
    }

	public EntitySniperSkeleton(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.99F);
        this.setCombatTask();
	}
	
	@Override
	public <T extends IAnimated> AnimationHandler<T> getAnimationHandler() {
		return EntitySniperSkeleton.animHandler;
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
	    this.tasks.addTask(2, new EntityAIRestrictSun(this));
	    this.tasks.addTask(2, new EntityAIForestSkeletonMoveToTree(this, SkeletonsConfig.skeletons.sniper.radius, 1.0D));
	    this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
	    this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
	    this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
	    this.tasks.addTask(6, new EntityAILookIdle(this));
	    this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
	    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	    this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	}

	@Override
	public double getYOffset() {
		return -0.43D;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16f);
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_SKELETON;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setCombatTask();
    }
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.forestBow));
		this.setDropChance(EntityEquipmentSlot.MAINHAND, (float) SkeletonsConfig.skeletons.sniper.bowDropChance);
		this.setCombatTask();
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {
        super.setItemStackToSlot(slotIn, stack);

        if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND)
        {
            this.setCombatTask();
        }
    }
	
	public void setCombatTask()
    {
        if (this.world != null && !this.world.isRemote)
        {
            this.tasks.removeTask(this.aiAttackOnCollide);
            this.tasks.removeTask(this.aiArrowAttack);
            ItemStack itemstack = this.getHeldItemMainhand();

            if (itemstack.getItem() == ModItems.forestBow)
            {
                int i = 20;

                if (this.world.getDifficulty() != EnumDifficulty.HARD)
                {
                    i = 40;
                }

                this.aiArrowAttack.setAttackCooldown(i);
                this.tasks.addTask(4, this.aiArrowAttack);
            }
            else
            {
                this.tasks.addTask(4, this.aiAttackOnCollide);
            }
        }
    }
	

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {
        EntityArrow entityarrow = this.getArrow(distanceFactor);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) SkeletonsConfig.skeletons.sniper.inaccuracy);
        this.playSound(ModSounds.forest_skeleton_shoot, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }
    
    @Override
    protected void onDeathUpdate() {
    	super.onDeathUpdate();
    	if (this.isWorldRemote() && !this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_death", this) && !flag){
    		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
    		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
    		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_holding_bow", this);
    		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "lookat", this);
            this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_death", 0, this);
            flag = true;
    	}
    }
    
    @Override
    public void onLivingUpdate() {
    	super.onLivingUpdate();
    	if (this.world.isDaytime() && !this.world.isRemote)
        {
            float f = this.getBrightness();
            BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, Math.round(this.posY), this.posZ);

            if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos))
            {
                boolean flag = true;
                ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                if (!itemstack.isEmpty())
                {
                    if (itemstack.isItemStackDamageable())
                    {
                        itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));

                        if (itemstack.getItemDamage() >= itemstack.getMaxDamage())
                        {
                            this.renderBrokenItemStack(itemstack);
                            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag)
                {
                    this.setFire(8);
                }
            }
        }
		if(!this.world.isRemote){
			this.setMoving(Boolean.valueOf(this.isMoving(this)));
		}
    	if(this.isWorldRemote()){
    		if(this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this) && !this.getMoving()){
    			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
    			if(!this.isSwingingArms()){
    				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
    			}
    		}
    		if(this.isSwingingArms() && !this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_holding_bow", this) && this.deathTime < 1){
    			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
        		this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_holding_bow", 0, this);
        	}
        	
        	if(!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this) && this.getMoving() && this.deathTime < 1 && !this.isRiding()){
        			this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_walk", 0, this);	
        	}
        	
        	if(!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk_hands", this) && !this.isSwingingArms() && this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this)){
        		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_holding_bow", this);
    			this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_walk_hands", 0, this);
    		}

        	if (!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "lookat", this) && this.deathTime < 1){
                this.getAnimationHandler().startAnimation(Reference.MOD_ID, "lookat", this);
        	}
        	if(!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "riding", this) && this.isRiding()){
        		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
        		this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_strafing", this);
        		this.getAnimationHandler().startAnimation(Reference.MOD_ID, "riding", this);  
        	}
    	}
    	
    }
	
	@Override
	protected EntityArrow getArrow(float distanceFactor) {
		EntityTippedArrow arrow = new EntityTippedArrow(this.world, this);
		arrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
		arrow.addEffect(new PotionEffect(MobEffects.POISON, SkeletonsConfig.skeletons.sniper.poison, 0, false, true));
		return arrow;
	}

}
