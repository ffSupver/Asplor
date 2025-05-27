package com.ffsupver.asplor.entity.ai;

import com.ffsupver.asplor.entity.custom.GlacioVillagerShaman;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;

public class GlacioVillagerShamanAttackGoal extends MeleeAttackGoal {
    private final GlacioVillagerShaman entity;
    private final int attackDelay = 20;
    private int tickToNextAttack = 20;
    private boolean shouldCount = false;

    public GlacioVillagerShamanAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.entity = (GlacioVillagerShaman) mob;
    }

    @Override
    public void start() {
        super.start();
        this.tickToNextAttack = 30;
        this.entity.setHighResistance(true);
    }

    @Override
    public void stop() {
        super.stop();
        entity.setAttacking(false);
        this.entity.setHighResistance(false);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCount){
            this.tickToNextAttack = Math.max(this.tickToNextAttack - 1,0);
        }
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if (isEnermyInAttackDistance(target,squaredDistance)){
            shouldCount = true;

            if (isTimeToPlayAnimation()){
                entity.setAttacking(true);
            }
            if (timeToAttack()){
                this.mob.getLookControl().lookAt(target.getX(),target.getY(),target.getZ());
                preformAttack(target);
            }
        }else {
            resetAttackCoolDown();
            shouldCount = false;
            entity.setAttacking(false);
            entity.attackAnimationTimeOut = 0;
        }
    }

    private void resetAttackCoolDown() {
        this.tickToNextAttack = this.getTickCount(attackDelay + 2);
    }

    private void preformAttack(LivingEntity target) {
        this.resetAttackCoolDown();
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(target);
    }

    private boolean timeToAttack() {
        return this.tickToNextAttack <= 0;
    }

    private boolean isTimeToPlayAnimation() {
        return this.tickToNextAttack <= attackDelay;
    }

    private boolean isEnermyInAttackDistance(LivingEntity target, double squaredDistance) {
        return squaredDistance <= this.getSquaredMaxAttackDistance(target);
    }
}
