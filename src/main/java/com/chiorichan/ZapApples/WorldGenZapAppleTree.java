package com.chiorichan.ZapApples;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import com.chiorichan.ZapApples.tileentity.TileEntityZapAppleLog;

public class WorldGenZapAppleTree extends WorldGenerator
{
	TileEntityZapAppleLog tile;
	Random rand = new Random();
	World worldObj;
	int[] basePos = { 0, 0, 0 };
	int heightLimit = 0;
	int height;
	double heightAttenuation = 0.618D;
	double branchDensity = 1.0D;
	double branchSlope = 0.381D;
	double scaleWidth = 1.0D;
	double leafDensity = 1.0D;
	int trunkSize = 1;
	int leafDistanceLimit = 4;
	int[][] leafNodes;
	static final byte[] otherCoordPairs = { 2, 0, 0, 1, 2, 1 };
	
	public WorldGenZapAppleTree(boolean flag)
	{
		super( flag );
	}
	
	public boolean generate( World world, Random rand, int x, int y, int z )
	{
		worldObj = world;
		rand.setSeed( rand.nextLong() );
		basePos[0] = x;
		basePos[1] = y;
		basePos[2] = z;
		
		if ( heightLimit == 0 )
		{
			heightLimit = ( 6 + rand.nextInt( 14 ) );
		}
		
		if ( !validTreeLocation() )
		{
			return false;
		}
		
		tile = new TileEntityZapAppleLog();
		
		generateLeafNodeList();
		generateLeaves();
		generateTrunk();
		generateLeafNodeBases();
		generateFlowers();
		
		worldObj.setBlock( x, y, z, ZapApples.zapAppleLog );
		worldObj.setTileEntity( x, y, z, tile );
		
		return true;
	}
	
	boolean validTreeLocation()
	{
		int[] var1 = { basePos[0], basePos[1], basePos[2] };
		int[] var2 = { basePos[0], basePos[1] + heightLimit - 1, basePos[2] };
		Block var3 = worldObj.getBlock( basePos[0], basePos[1] - 1, basePos[2] );
		
		if ( ( var3 != Block.getBlockById( 2 ) ) && ( var3 != Block.getBlockById( 3 ) ) )
		{
			return false;
		}
		
		int var4 = checkBlockLine( var1, var2 );
		
		if ( var4 == -1 )
		{
			return true;
		}
		if ( var4 < 6 )
		{
			return false;
		}
		
		heightLimit = var4;
		return true;
	}
	
	int checkBlockLine( int[] par1ArrayOfInteger, int[] par2ArrayOfInteger )
	{
		int[] var3 = new int[] { 0, 0, 0 };
		byte var4 = 0;
		byte var5;
		
		for ( var5 = 0; var4 < 3; ++var4 )
		{
			var3[var4] = par2ArrayOfInteger[var4] - par1ArrayOfInteger[var4];
			
			if ( Math.abs( var3[var4] ) > Math.abs( var3[var5] ) )
			{
				var5 = var4;
			}
		}
		
		if ( var3[var5] == 0 )
		{
			return -1;
		}
		else
		{
			byte var6 = otherCoordPairs[var5];
			byte var7 = otherCoordPairs[var5 + 3];
			byte var8;
			
			if ( var3[var5] > 0 )
			{
				var8 = 1;
			}
			else
			{
				var8 = -1;
			}
			
			double var9 = (double) var3[var6] / (double) var3[var5];
			double var11 = (double) var3[var7] / (double) var3[var5];
			int[] var13 = new int[] { 0, 0, 0 };
			int var14 = 0;
			int var15;
			
			for ( var15 = var3[var5] + var8; var14 != var15; var14 += var8 )
			{
				var13[var5] = par1ArrayOfInteger[var5] + var14;
				var13[var6] = MathHelper.floor_double( (double) par1ArrayOfInteger[var6] + (double) var14 * var9 );
				var13[var7] = MathHelper.floor_double( (double) par1ArrayOfInteger[var7] + (double) var14 * var11 );
				Block var16 = this.worldObj.getBlock( var13[0], var13[1], var13[2] );
				
				if ( var16 != Blocks.air && ( var14 != Block.getIdFromBlock( Blocks.leaves ) || var14 != Block.getIdFromBlock( ZapApples.zapAppleLeaves ) || var14 == Block.getIdFromBlock( Blocks.snow ) || var14 == Block.getIdFromBlock( Blocks.vine ) ) )
				{
					break;
				}
			}
			
			return var14 == var15 ? -1 : Math.abs( var14 );
		}
	}
	
	void generateLeafNodeList()
	{
		height = ( (int) ( heightLimit * heightAttenuation ) );
		
		if ( height >= heightLimit )
		{
			height = ( heightLimit - 1 );
		}
		
		int var1 = (int) ( 1.382D + Math.pow( leafDensity * heightLimit / 13.0D, 2.0D ) );
		
		if ( var1 < 1 )
		{
			var1 = 1;
		}
		
		int[][] var2 = new int[var1 * heightLimit][4];
		int var3 = basePos[1] + heightLimit - leafDistanceLimit;
		int var4 = 1;
		int var5 = basePos[1] + height;
		int var6 = var3 - basePos[1];
		var2[0][0] = basePos[0];
		var2[0][1] = var3;
		var2[0][2] = basePos[2];
		var2[0][3] = var5;
		var3--;
		
		while ( var6 >= 0 )
		{
			int var7 = 0;
			float var8 = layerSize( var6 );
			
			if ( var8 < 0.0F )
			{
				var3--;
				var6--;
			}
			else
			{
				for ( double var9 = 0.5D; var7 < var1; var7++ )
				{
					double var11 = scaleWidth * var8 * ( rand.nextFloat() + 0.328D );
					double var13 = rand.nextFloat() * 2.0D * 3.141592653589793D;
					int var15 = MathHelper.floor_double( var11 * Math.sin( var13 ) + basePos[0] + var9 );
					int var16 = MathHelper.floor_double( var11 * Math.cos( var13 ) + basePos[2] + var9 );
					int[] var17 = { var15, var3, var16 };
					int[] var18 = { var15, var3 + leafDistanceLimit, var16 };
					
					if ( checkBlockLine( var17, var18 ) == -1 )
					{
						int[] var19 = { basePos[0], basePos[1], basePos[2] };
						double var20 = Math.sqrt( Math.pow( Math.abs( basePos[0] - var17[0] ), 2.0D ) + Math.pow( Math.abs( basePos[2] - var17[2] ), 2.0D ) );
						double var22 = var20 * branchSlope;
						
						if ( var17[1] - var22 > var5 )
						{
							var19[1] = var5;
						}
						else
						{
							var19[1] = ( (int) ( var17[1] - var22 ) );
						}
						
						if ( checkBlockLine( var19, var17 ) == -1 )
						{
							var2[var4][0] = var15;
							var2[var4][1] = var3;
							var2[var4][2] = var16;
							var2[var4][3] = var19[1];
							var4++;
						}
					}
				}
				
				var3--;
				var6--;
			}
		}
		
		leafNodes = new int[var4][4];
		System.arraycopy( var2, 0, leafNodes, 0, var4 );
	}
	
	float layerSize( int par1 )
	{
		if ( par1 < heightLimit * 0.3D )
		{
			return -1.618F;
		}
		
		float var2 = heightLimit / 2.0F;
		float var3 = heightLimit / 2.0F - par1;
		float var4;
		if ( var3 == 0.0F )
		{
			var4 = var2;
		}
		else
		{
			if ( Math.abs( var3 ) >= var2 )
			{
				var4 = 0.0F;
			}
			else
			{
				var4 = (float) Math.sqrt( Math.pow( Math.abs( var2 ), 2.0D ) - Math.pow( Math.abs( var3 ), 2.0D ) );
			}
		}
		var4 *= 0.5F;
		return var4;
	}
	
	void generateLeaves()
	{
		int var1 = 0;
		
		for ( int var2 = leafNodes.length; var1 < var2; var1++ )
		{
			int var3 = leafNodes[var1][0];
			int var4 = leafNodes[var1][1];
			int var5 = leafNodes[var1][2];
			generateLeafNode( var3, var4, var5 );
		}
	}
	
	void generateLeafNode( int par1, int par2, int par3 )
	{
		int var4 = par2;
		
		for ( int var5 = par2 + leafDistanceLimit; var4 < var5; var4++ )
		{
			float var6 = leafSize( var4 - par2 );
			genTreeLayer( par1, var4, par3, var6, (byte) 1, ZapApples.zapAppleLeaves );
		}
	}
	
	float leafSize( int par1 )
	{
		return ( par1 >= 0 ) && ( par1 < leafDistanceLimit ) ? 2.0F : ( par1 != 0 ) && ( par1 != leafDistanceLimit - 1 ) ? 3.0F : -1.0F;
	}
	
	void genTreeLayer( int par1, int par2, int par3, float par4, byte par5, Block par6 )
	{
		int var7 = (int) ( (double) par4 + 0.618D );
		byte var8 = otherCoordPairs[par5];
		byte var9 = otherCoordPairs[par5 + 3];
		int[] var10 = new int[] { par1, par2, par3 };
		int[] var11 = new int[] { 0, 0, 0 };
		int var12 = -var7;
		int var13 = -var7;
		
		for ( var11[par5] = var10[par5]; var12 <= var7; ++var12 )
		{
			var11[var8] = var10[var8] + var12;
			var13 = -var7;
			
			while ( var13 <= var7 )
			{
				double var15 = Math.pow( (double) Math.abs( var12 ) + 0.5D, 2.0D ) + Math.pow( (double) Math.abs( var13 ) + 0.5D, 2.0D );
				
				if ( var15 > (double) ( par4 * par4 ) )
				{
					++var13;
				}
				else
				{
					var11[var9] = var10[var9] + var13;
					Block var14 = this.worldObj.getBlock( var11[0], var11[1], var11[2] );
					
					if ( var14 != Blocks.air && ( var14 != ZapApples.zapAppleLeaves || var14 == Blocks.snow || var14 == Blocks.leaves || var14 == Blocks.vine ) )
					{
						++var13;
					}
					else
					{
						tile.leafPositions.add( new OrderedTriple( var11[0], var11[1], var11[2] ) );
						// this.setBlockAndMetadata(this.worldObj, var11[0], var11[1], var11[2], par6, 0);
						++var13;
					}
				}
			}
		}
	}
	
	void generateTrunk()
	{
		int var1 = basePos[0];
		int var2 = basePos[1];
		int var3 = basePos[1] + height;
		int var4 = basePos[2];
		int[] var5 = { var1, var2, var4 };
		int[] var6 = { var1, var3, var4 };
		placeBlockLine( var5, var6 );
		
		if ( trunkSize == 2 )
		{
			var5[0] += 1;
			var6[0] += 1;
			placeBlockLine( var5, var6 );
			var5[2] += 1;
			var6[2] += 1;
			placeBlockLine( var5, var6 );
			var5[0] += -1;
			var6[0] += -1;
			placeBlockLine( var5, var6 );
		}
	}
	
	void placeBlockLine( int[] par1ArrayOfInteger, int[] par2ArrayOfInteger )
	{
		int[] var4 = new int[] { 0, 0, 0 };
		byte var5 = 0;
		byte var6;
		
		for ( var6 = 0; var5 < 3; ++var5 )
		{
			var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];
			
			if ( Math.abs( var4[var5] ) > Math.abs( var4[var6] ) )
			{
				var6 = var5;
			}
		}
		
		if ( var4[var6] != 0 )
		{
			byte var7 = otherCoordPairs[var6];
			byte var8 = otherCoordPairs[var6 + 3];
			byte var9;
			
			if ( var4[var6] > 0 )
			{
				var9 = 1;
			}
			else
			{
				var9 = -1;
			}
			
			double var10 = (double) var4[var7] / (double) var4[var6];
			double var12 = (double) var4[var8] / (double) var4[var6];
			int[] var14 = new int[] { 0, 0, 0 };
			int var15 = 0;
			
			for ( int var16 = var4[var6] + var9; var15 != var16; var15 += var9 )
			{
				var14[var6] = MathHelper.floor_double( (double) ( par1ArrayOfInteger[var6] + var15 ) + 0.5D );
				var14[var7] = MathHelper.floor_double( (double) par1ArrayOfInteger[var7] + (double) var15 * var10 + 0.5D );
				var14[var8] = MathHelper.floor_double( (double) par1ArrayOfInteger[var8] + (double) var15 * var12 + 0.5D );
				byte var17 = 0;
				int var18 = Math.abs( var14[0] - par1ArrayOfInteger[0] );
				int var19 = Math.abs( var14[2] - par1ArrayOfInteger[2] );
				int var20 = Math.max( var18, var19 );
				
				if ( var20 > 0 )
				{
					if ( var18 == var20 )
					{
						var17 = 4;
					}
					else if ( var19 == var20 )
					{
						var17 = 8;
					}
				}
				tile.leafPositions.remove( new OrderedTriple( var14[0], var14[1], var14[2] ) );
				tile.logPositions.add( new OrderedTriple( var14[0], var14[1], var14[2] ) );
				setBlockAndNotifyAdequately( this.worldObj, var14[0], var14[1], var14[2], ZapApples.zapAppleLog, var17 );
			}
		}
	}
	
	void generateLeafNodeBases()
	{
		int var1 = 0;
		int var2 = leafNodes.length;
		
		for ( int[] var3 = { basePos[0], basePos[1], basePos[2] }; var1 < var2; var1++ )
		{
			int[] var4 = leafNodes[var1];
			int[] var5 = { var4[0], var4[1], var4[2] };
			var3[1] = var4[3];
			int var6 = var3[1] - basePos[1];
			
			if ( leafNodeNeedsBase( var6 ) )
			{
				placeBlockLine( var3, var5 );
			}
		}
	}
	
	boolean leafNodeNeedsBase( int par1 )
	{
		return par1 >= heightLimit * 0.2D;
	}
	
	void generateFlowers()
	{
		for ( int y = basePos[1]; y < basePos[1] + heightLimit + 3; y++ )
		{
			for ( int x = basePos[0] - leafDistanceLimit - 3; x < basePos[0] + leafDistanceLimit + 3; x++ )
			{
				for ( int z = basePos[2] - leafDistanceLimit - 3; z < basePos[2] + leafDistanceLimit + 3; z++ )
				{
					Block b = worldObj.getBlock( x, y, z );
					OrderedTriple north = new OrderedTriple( x, y, z + 1 );
					OrderedTriple south = new OrderedTriple( x, y, z - 1 );
					OrderedTriple east = new OrderedTriple( x - 1, y, z );
					OrderedTriple west = new OrderedTriple( x + 1, y, z );
					
					OrderedTriple pos = new OrderedTriple( x, y, z );
					if ( ( rand.nextInt( 8 ) == 1 ) && ( ( b == Block.getBlockById( 0 ) ) || ( b == Block.getBlockFromName( "snow" ) ) || ( b == Block.getBlockFromName( "leaves" ) ) || ( b == Block.getBlockFromName( "vine" ) ) ) && ( !tile.leafPositions.contains( pos ) ) )
					{
						int side = 0;
						
						if ( tile.leafPositions.contains( north ) )
						{
							side = ForgeDirection.NORTH.ordinal();
						}
						else if ( tile.leafPositions.contains( south ) )
						{
							side = ForgeDirection.SOUTH.ordinal();
						}
						else if ( tile.leafPositions.contains( east ) )
						{
							side = ForgeDirection.EAST.ordinal();
						}
						else if ( tile.leafPositions.contains( west ) )
						{
							side = ForgeDirection.WEST.ordinal();
						}
						
						if ( side != 0 )
						{
							tile.applePositions.put( pos, Integer.valueOf( side ) );
						}
					}
				}
			}
		}
	}
}
