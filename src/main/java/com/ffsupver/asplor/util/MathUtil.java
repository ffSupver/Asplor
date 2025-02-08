package com.ffsupver.asplor.util;

import net.minecraft.util.math.random.Random;

public final class MathUtil {
    public static long fromEtoAE(long E) {
        return E * 2;
    }

    public static double fromAEtoE(double AE) {
        return AE / 2;
    }

    public static float getRandomFloat(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public enum CubeNode{
        SEU(1,1,1),
        SED(1,-1,1),
        SWD(-1,-1,1),
        SWU(-1,1,1),
        NEU(1,1,-1),
        NWU(-1,1,-1),
        NED(1,-1,-1),
        NWD(-1,-1,-1);
        public final int x;
        public final int y;
        public final int z;

        CubeNode(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public enum CubeFace{
        //点顺序为逆时针
        U(CubeNode.NEU,CubeNode.SEU,CubeNode.SWU,CubeNode.NWU),
        D(CubeNode.NED,CubeNode.NWD,CubeNode.SWD,CubeNode.SED),
        E(CubeNode.NED,CubeNode.SED,CubeNode.SEU,CubeNode.NEU),
        W(CubeNode.NWD,CubeNode.NWU,CubeNode.SWU,CubeNode.SWD),
        N(CubeNode.NEU,CubeNode.NWU,CubeNode.NWD,CubeNode.NED),
        S(CubeNode.SEU,CubeNode.SED,CubeNode.SWD,CubeNode.SWU);
        public final CubeNode[] nodes;

        CubeFace(CubeNode[] nodes) {
            this.nodes = nodes;
        }

        CubeFace(CubeNode cubeNode, CubeNode cubeNode1, CubeNode cubeNode2, CubeNode cubeNode3) {
            this(new CubeNode[]{cubeNode,cubeNode1,cubeNode2,cubeNode3});
        }
    }
}