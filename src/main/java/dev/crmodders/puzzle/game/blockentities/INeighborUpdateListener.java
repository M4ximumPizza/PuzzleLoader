package dev.crmodders.puzzle.game.blockentities;

import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.constants.Direction;

public interface INeighborUpdateListener {

    void onNeighborUpdate(Direction face, BlockState block, BlockEntity entity);

}