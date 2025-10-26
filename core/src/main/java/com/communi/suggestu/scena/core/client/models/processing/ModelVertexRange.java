package com.communi.suggestu.scena.core.client.models.processing;

import com.mojang.blaze3d.vertex.VertexFormatElement;

public class ModelVertexRange extends BaseModelReader
{
    private float minSumX = 1;
    private float minSumY = 1;
    private float minSumZ = 1;
    private float maxSumX = 0;
    private float maxSumY = 0;
    private float maxSumZ   = 0;
    private int   vertCount = 0;

    public float getLargestRange()
    {
        if ( vertCount == 0 )
        {
            return 0;
        }

        final float x = maxSumX - minSumX;
        final float y = maxSumY - minSumY;
        final float z = maxSumZ - minSumZ;
        return Math.max( x, Math.max( y, z ) );
    }

    @Override
    public void put(
      final int vertNum,
      final int element,
      final float... data )
    {
        final VertexFormatElement e = getVertexFormat().getElements().get(element);
        if ( e.usage() == VertexFormatElement.Usage.POSITION )
        {
            if ( vertCount == 0 )
            {
                minSumX = data[0];
                minSumY = data[1];
                minSumZ = data[2];
                maxSumX = data[0];
                maxSumY = data[1];
                maxSumZ = data[2];
            }
            else
            {
                minSumX = Math.min( data[0], minSumX );
                minSumY = Math.min( data[1], minSumY );
                minSumZ = Math.min( data[2], minSumZ );
                maxSumX = Math.max( data[0], maxSumX );
                maxSumY = Math.max( data[1], maxSumY );
                maxSumZ = Math.max( data[2], maxSumZ );
            }

            ++vertCount;
        }
    }

}
