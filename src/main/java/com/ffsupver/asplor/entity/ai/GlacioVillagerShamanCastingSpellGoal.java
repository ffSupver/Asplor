package com.ffsupver.asplor.entity.ai;

import com.ffsupver.asplor.entity.custom.GlacioVillagerShaman;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class GlacioVillagerShamanCastingSpellGoal extends Goal {
    private final GlacioVillagerShaman actor;
    private final double speed;
    private final float squaredRange;
    private int tickToAttack;

    public GlacioVillagerShamanCastingSpellGoal(GlacioVillagerShaman actor, double speed, float range) {
        this.actor = actor;
        this.speed = speed;
        this.squaredRange = range * range;
    }

    @Override
    public boolean canStart() {
        return actor.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart() || !this.actor.getNavigation().isIdle();
    }

    @Override
    public void start() {
        super.start();
        tickToAttack = 100;
    }

    @Override
    public void stop() {
        super.stop();
        actor.setMeteoriteAttacking(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean canSee = this.actor.getVisibilityCache().canSee(livingEntity);
            boolean tooClose = d < (double)this.squaredRange;
            if (tooClose){
                this.actor.getNavigation().startMovingTo(livingEntity,-speed);
            }else if (canSee){
                this.actor.getNavigation().stop();
                this.actor.getLookControl().lookAt(livingEntity,30,30);

                if (tickToAttack <= 0){
                    attack(livingEntity);
                }else {
                    tickToAttack--;
                }

                this.actor.setMeteoriteAttacking(timeToPlayAnimation());
            }

            if (tooClose || !canSee){
                this.actor.setMeteoriteAttacking(false);
            }


        }else {
            this.stop();
        }
    }

    private void attack(LivingEntity target){
        this.actor.tryAttackWithMeteorite(target);
        this.tickToAttack = 100;
        this.actor.setMeteoriteAttacking(false);
    }


    private boolean timeToPlayAnimation() {
        return tickToAttack <= 40 && tickToAttack > 0;
    }
}
