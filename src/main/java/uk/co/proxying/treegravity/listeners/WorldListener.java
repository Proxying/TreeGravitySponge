package uk.co.proxying.treegravity.listeners;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

/**
 * Copyright © 2019 Property of PROXYING ENTERPRISES LTD
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */
public class WorldListener {

    @Listener
    public void onPlayerBreakLog(ChangeBlockEvent.Break event, @Root Player player) {
        BlockSnapshot block = event.getTransactions().get(0).getOriginal();
        if (block.getState().getType() == BlockTypes.LOG || block.getState().getType() == BlockTypes.LOG2) {
            BlockState toTest;

            boolean fakeTree = false;
            int leavesCount = 0;

            for (int x = -4; x < 5; x++) {
                for (int y = -5; y < 10; y++) {
                    for (int z = -4; z < 5; z++) {
                        toTest = block.getLocation().get().copy().add(x, y, z).getBlock();
                        if (toTest.getType() == BlockTypes.LEAVES || toTest.getType() == BlockTypes.LEAVES2) {
                            leavesCount++;
                            if (toTest.getTrait("decayable").isPresent() && toTest.getTrait("decayable").get().toString().equalsIgnoreCase("false")) {
                                fakeTree = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (fakeTree || leavesCount == 0) {
                return;
            }

            if (block.getLocation().isPresent()) {
                checkAndProcessNearbyLogs(block.getLocation().get(), 0);
            }
        }
    }

    private void checkAndProcessNearbyLogs(Location location, int currentCount) {
        int airCount = 0;
        Location testLocation;
        BlockState toTest;
        for (int i = 0; i <= (15 - currentCount); i++) {
            if (airCount >= 3) {
                break;
            }
            currentCount ++;
            testLocation = location.getBlockRelative(Direction.UP);
            toTest = testLocation.getBlock();
            if (toTest.getType() == BlockTypes.AIR) {
                airCount++;
            } else if (toTest.getType() == BlockTypes.LOG || toTest.getType() == BlockTypes.LOG2) {
                Vector3d point = testLocation.getBlockPosition().toDouble().add(0.5d, 0, 0.5d);
                Entity fallingBlock = testLocation.getExtent().createEntity(EntityTypes.FALLING_BLOCK, point);
                BlockState tempClone = toTest.copy();
                fallingBlock.offer(Keys.FALLING_BLOCK_STATE, tempClone);
                fallingBlock.offer(Keys.VELOCITY, new Vector3d(0.0, 0.0, 0.0));
                testLocation.getExtent().spawnEntity(fallingBlock);
                checkAndProcessNearbyLogs(testLocation, currentCount);
                break;
            }
        }
    }
}
