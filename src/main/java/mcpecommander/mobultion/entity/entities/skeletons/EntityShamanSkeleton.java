package mcpecommander.mobultion.entity.entities.skeletons;

import mcpecommander.mobultion.Reference;
import mcpecommander.mobultion.entity.animation.AnimationLookAt;
import mcpecommander.mobultion.entity.animation.AnimationRiding;
import mcpecommander.mobultion.entity.entityAI.skeletonsAI.EntityAIShamanSkeletonHeal;
import mcpecommander.mobultion.entity.entityAI.skeletonsAI.EntityAIShamanSkeletonTarget;
import mcpecommander.mobultion.init.ModItems;
import mcpecommander.mobultion.mobConfigs.SkeletonsConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityShamanSkeleton extends EntityAnimatedSkeleton {
	boolean flag = false;

	static {
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_death", "forest_skeleton", false);
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_walk", "forest_skeleton", true);
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_walk_hands", "forest_skeleton", true);
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "skeleton_healing", "forest_skeleton", true);
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "lookat", new AnimationLookAt("Head"));
		EntityShamanSkeleton.animHandler.addAnim(Reference.MOD_ID, "riding", new AnimationRiding());
	}

	public EntityShamanSkeleton(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 1.99F);
	}
	
	@Override
	public boolean getCanSpawnHere() {
		return this.world.getEntitiesWithinAABB(EntityShamanSkeleton.class, this.getEntityBoundingBox().grow(10)).isEmpty() && super.getCanSpawnHere();
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityPlayer.class, 4.5F, 1.0D, 1.2D));
		this.tasks.addTask(4, new EntityAIShamanSkeletonHeal(this, 1.2D));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1,
				new EntityAIShamanSkeletonTarget(this, false, EntitySkeletonRemains.class, EntityJokerSkeleton.class,
						EntitySniperSkeleton.class, EntityMagmaSkeleton.class, EntityWitheringSkeleton.class,
						EntitySkeleton.class, EntityStray.class));
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return Reference.LootTables.ENTITYSHAMANSKELETON;
	}

	@Override
	public double getYOffset() {
		return -0.43D;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26f);
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.healingWand));
		this.setDropChance(EntityEquipmentSlot.MAINHAND, (float) SkeletonsConfig.skeletons.shaman.wandDropChance);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
	}

	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
		if (this.isWorldRemote()
				&& !this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_death", this) && !flag) {
			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_healing", this);
			this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "lookat", this);
			this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_death", 0, this);
			flag = true;
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			this.setMoving(Boolean.valueOf(this.isMoving(this)));
		}
		if (this.isWorldRemote()) {
			if (this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this)
					&& !this.getMoving()) {
				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
				if (!this.isSwingingArms()) {
					this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
				}
			}
			if (this.isSwingingArms()
					&& !this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_healing", this)
					&& this.deathTime < 1) {
				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk_hands", this);
				this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_healing", 0, this);
			}

			if (!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this)
					&& this.getMoving() && this.deathTime < 1 && !this.isRiding()) {
				this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_walk", 0, this);
			}
			if (this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_healing", this)
					&& !this.isSwingingArms()) {
				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_healing", this);
			}
			if (!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk_hands", this)
					&& !this.isSwingingArms()
					&& this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "skeleton_walk", this)) {
				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_healing", this);
				this.getAnimationHandler().startAnimation(Reference.MOD_ID, "skeleton_walk_hands", 0, this);
			}

			if (!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "lookat", this) && this.deathTime < 1) {
				this.getAnimationHandler().startAnimation(Reference.MOD_ID, "lookat", this);
			}
			if (!this.getAnimationHandler().isAnimationActive(Reference.MOD_ID, "riding", this) && this.isRiding()) {
				this.getAnimationHandler().stopAnimation(Reference.MOD_ID, "skeleton_walk", this);
				this.getAnimationHandler().startAnimation(Reference.MOD_ID, "riding", this);
			}
		}

	}

	@Override
	protected EntityArrow getArrow(float distanceFactor) {
		return null;
	}

}
