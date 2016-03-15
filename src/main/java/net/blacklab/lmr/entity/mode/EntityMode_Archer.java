package net.blacklab.lmr.entity.mode;

import java.util.List;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.achievements.LMMNX_Achievements;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.ai.EntityAILMHurtByTarget;
import net.blacklab.lmr.entity.ai.EntityAILMNearestAttackableTarget;
import net.blacklab.lmr.inventory.InventoryLittleMaid;
import net.blacklab.lmr.util.TriggerSelect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityMode_Archer extends EntityModeBase {

	public static final int mmode_Archer		= 0x0083;
	public static final int mmode_Blazingstar	= 0x00c3;
	
	
	@Override
	public int priority() {
		return 3200;
	}

	public EntityMode_Archer(EntityLittleMaid pEntity) {
		super(pEntity);
		isAnytimeUpdate = true;
	}

	@Override
	public void init() {
		// 登録モードの名称追加
		/* langファイルに移動
		ModLoader.addLocalization("littleMaidMob.mode.Archer", "Archer");
		ModLoader.addLocalization("littleMaidMob.mode.F-Archer", "F-Archer");
		ModLoader.addLocalization("littleMaidMob.mode.T-Archer", "T-Archer");
		ModLoader.addLocalization("littleMaidMob.mode.D-Archer", "D-Archer");
//		ModLoader.addLocalization("littleMaidMob.mode.Archer", "ja_JP", "射手");
		ModLoader.addLocalization("littleMaidMob.mode.Blazingstar", "Blazingstar");
		ModLoader.addLocalization("littleMaidMob.mode.F-Blazingstar", "F-Blazingstar");
		ModLoader.addLocalization("littleMaidMob.mode.T-Blazingstar", "T-Blazingstar");
		ModLoader.addLocalization("littleMaidMob.mode.D-Blazingstar", "D-Blazingstar");
//		ModLoader.addLocalization("littleMaidMob.mode.Blazingstar", "ja_JP", "刃鳴散らす者");
		*/
		TriggerSelect.appendTriggerItem(null, "Bow", "");
		TriggerSelect.appendTriggerItem(null, "Arrow", "");
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		// Archer:0x0083
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = new EntityAITasks(owner.aiProfiler);
		
//		ltasks[1].addTask(1, new EntityAIOwnerHurtByTarget(owner));
//		ltasks[1].addTask(2, new EntityAIOwnerHurtTarget(owner));
		ltasks[1].addTask(3, new EntityAILMHurtByTarget(owner, true));
		ltasks[1].addTask(4, new EntityAILMNearestAttackableTarget(owner, EntityLivingBase.class, 0, true));
		
		owner.addMaidMode(ltasks, "Archer", mmode_Archer);
		
		
		// Blazingstar:0x00c3
		EntityAITasks[] ltasks2 = new EntityAITasks[2];
		ltasks2[0] = pDefaultMove;
		ltasks2[1] = new EntityAITasks(owner.aiProfiler);
		
		ltasks2[1].addTask(1, new EntityAILMHurtByTarget(owner, true));
		ltasks2[1].addTask(2, new EntityAILMNearestAttackableTarget(owner, EntityLivingBase.class, 0, true));
		
		owner.addMaidMode(ltasks2, "Blazingstar", mmode_Blazingstar);
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			if (litemstack.getItem() instanceof ItemBow || TriggerSelect.checkWeapon(owner.getMaidMaster(), "Bow", litemstack)) {
				if (owner.maidInventory.getInventorySlotContainItem(ItemFlintAndSteel.class) > -1) {
					owner.setMaidMode("Blazingstar");
					if (LMMNX_Achievements.ac_BlazingStar != null) {
						pentityplayer.triggerAchievement(LMMNX_Achievements.ac_BlazingStar);
					}
				} else {
					owner.setMaidMode("Archer");
					if (LMMNX_Achievements.ac_Archer != null) {
						pentityplayer.triggerAchievement(LMMNX_Achievements.ac_Archer);
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
		case mmode_Archer :
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(true);
			owner.setBloodsuck(false);
			return true;
		case mmode_Blazingstar :
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(true);
			owner.setBloodsuck(true);
			return true;
		}
		
		return false;
	}
	
	@Override
	public int getNextEquipItem(int pMode) {
		int li;
		ItemStack litemstack;

		// モードに応じた識別判定、速度優先
		switch (pMode) {
		case mmode_Archer :
		case mmode_Blazingstar :
			for (li = 0; li < InventoryLittleMaid.maxInventorySize; li++) {
				litemstack = owner.maidInventory.getStackInSlot(li);
				if (litemstack == null) continue;

				// 射手
				if (litemstack.getItem() instanceof ItemBow || TriggerSelect.checkWeapon(owner.getMaidMaster(), "Bow", litemstack)) {
					return li;
				}
			}
			break;
		}

		return -1;
	}
	
	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		String ls = owner.getMaidMaster();
		return (pItemStack.getItem() instanceof ItemBow) || (pItemStack.getItem() == Items.arrow) 
				|| TriggerSelect.checkWeapon(ls, "Bow", pItemStack) || TriggerSelect.checkWeapon(ls, "Arrow", pItemStack);
	}

	@Override
	public void onUpdate(int pMode) {
		switch (pMode) {
		case mmode_Archer:
		case mmode_Blazingstar:
			owner.getWeaponStatus();
//			updateGuns();
			break;
		}

	}

	@Override
	public void updateAITick(int pMode) {
		switch (pMode) {
		case mmode_Archer:
//			owner.getWeaponStatus();
//			updateGuns();
			break;
		case mmode_Blazingstar:
//			owner.getWeaponStatus();
//			updateGuns();
			// Blazingstarの特殊効果
			World lworld = owner.worldObj;
			List<Entity> llist = lworld.getEntitiesWithinAABB(Entity.class, owner.getEntityBoundingBox().expand(16D, 16D, 16D));
			for (int li = 0; li < llist.size(); li++) {
				Entity lentity = llist.get(li); 
				if (lentity.isEntityAlive() && lentity.isBurning() && owner.getRNG().nextFloat() > 0.9F) {
					// 発火！
					int lx = (int)owner.posX;
					int ly = (int)owner.posY;
					int lz = (int)owner.posZ;
					if (lworld.isAirBlock(new BlockPos(lx, ly, lz)) || lworld.getBlockState(new BlockPos(lx, ly, lz)).getBlock().getMaterial().getCanBurn()) {
						lworld.playSoundEffect(lx + 0.5D, ly + 0.5D, lz + 0.5D, "fire.ignite", 1.0F, owner.getRNG().nextFloat() * 0.4F + 0.8F);
						lworld.setBlockState(new BlockPos(lx, ly, lz), Blocks.fire.getDefaultState());
					}
				}
			}
			break;
		}
	}

	protected void updateGuns() {
		if (owner.getAttackTarget() == null || !owner.getAttackTarget().isEntityAlive()) {
			// 対象が死んだ
			if (!owner.weaponReload) {
				if (owner.maidAvatar.isUsingItem()) {
					// ターゲットが死んでいる時はアイテムの使用をクリア
					if (owner.getAvatarIF().getIsItemReload()) {
						owner.maidAvatar.stopUsingItem();
						LittleMaidReengaged.Debug(String.format("id:%d cancel reload.", owner.getEntityId()));
					} else {
						owner.maidAvatar.clearItemInUse();
						LittleMaidReengaged.Debug(String.format("id:%d clear.", owner.getEntityId()));
					}
				}
			} else {
				owner.mstatAimeBow = true;
			}
		}
		if (owner.weaponReload && !owner.maidAvatar.isUsingItem()) {
			// 特殊リロード
			owner.maidInventory.getCurrentItem().useItemRightClick(owner.worldObj, owner.maidAvatar);
			LittleMaidReengaged.Debug("id:%d force reload.", owner.getEntityId());
			owner.mstatAimeBow = true;
		}

	}
	
}