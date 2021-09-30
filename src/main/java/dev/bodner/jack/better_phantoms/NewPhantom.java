package dev.bodner.jack.better_phantoms;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NewPhantom extends Phantom {
    public NewPhantom(EntityType<? extends Phantom> entitytypes, Level world) {
        super(entitytypes, world);
    }

    @Override
    public void tick(){
        super.tick();
//        try {
//            System.out.println("Target: " + this.getTarget());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    protected void registerGoals(){
        super.registerGoals();
//        this.targetSelector = new GoalSelector(this.level.getProfilerSupplier());
////        Goal goalRemove = null;
//        for (WrappedGoal goal: this.targetSelector.getAvailableGoals()){
//            System.out.println(goal.getGoal());
//        }
//        System.out.println();
//////        this.targetSelector.getAvailableGoals()
////        this.targetSelector.removeGoal(goalRemove);
        this.targetSelector.addGoal(1, new NewPhantomAttackPlayerTargetGoal());
//        for (WrappedGoal goal: this.targetSelector.getAvailableGoals()){
//            System.out.println(goal.getGoal().toString());
//        }
//        System.out.println();
    }

    private class NewPhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = 20;

        NewPhantomAttackPlayerTargetGoal() {
        }

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
            } else {
                this.nextScanTick = 60;
//                List<Player> list = NewPhantom.this.level.getNearbyPlayers(this.attackTargeting, NewPhantom.this, NewPhantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                List<LivingEntity> list = NewPhantom.this.level.getNearbyEntities(LivingEntity.class, this.attackTargeting, NewPhantom.this, NewPhantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                ArrayList<LivingEntity> glowList = new ArrayList<>();
                ArrayList<LivingEntity> finalList = new ArrayList<>();
                for (LivingEntity entity : list){
//                    float distanceTo = NewPhantom.this.distanceTo(entity);
                    if (entity.hasEffect(MobEffect.byId(24))){
                        glowList.add(entity);
                    }
                    else if (entity.getType().equals(EntityType.PLAYER) || entity.getType().equals(EntityType.GLOW_SQUID)){
                        if (entity.level.canSeeSky(entity.getOnPos())) {
                            finalList.add(entity);
                        }
                    }
                }

                glowList.sort((o1, o2) -> (int)(NewPhantom.this.distanceTo(o2) - NewPhantom.this.distanceTo(o1)));
                finalList.sort((o1, o2) -> (int)(NewPhantom.this.distanceTo(o2) - NewPhantom.this.distanceTo(o1)));

                finalList.addAll(0, glowList);

                if (!finalList.isEmpty()) {
//                    System.out.println(finalList);
                    for (LivingEntity entity : list) {
                        if (NewPhantom.this.canAttack(entity, TargetingConditions.DEFAULT)) {
                            NewPhantom.this.setGoalTarget(entity, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
//                            System.out.println("target: "+NewPhantom.this.getTarget());
//                            System.out.println("new target: " + list);
//                            if (NewPhantom.this.setGoalTarget(entity, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true)){
//                                System.out.println("New Target Successfully Acquired.\n");
//                            }
//                            else {
//                                System.out.println("Failed to apply new target\n");
//                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            LivingEntity entityliving = NewPhantom.this.getTarget();
            return entityliving != null && NewPhantom.this.canAttack(entityliving, TargetingConditions.DEFAULT);
        }
    }

}
