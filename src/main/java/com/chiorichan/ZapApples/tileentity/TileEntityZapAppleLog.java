package com.chiorichan.ZapApples.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.chiorichan.ZapApples.OrderedTriple;
import com.chiorichan.ZapApples.ZapApples;
import com.chiorichan.ZapApples.events.DayChangeListener;
import com.chiorichan.ZapApples.events.DaytimeManager;
import com.chiorichan.ZapApples.network.PacketHandler;
import com.chiorichan.ZapApples.network.packet.client.SendEffectsPacket;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityZapAppleLog extends TileEntity implements DayChangeListener
{
	private Random rand;
	private int index;
	private int delay;
	private List<OrderedTriple> leafCopy;
	private List<OrderedTriple> appleCopy;
	public boolean[] finished;
	public List<OrderedTriple> leafPositions;
	public List<OrderedTriple> logPositions;
	public HashMap<OrderedTriple, Integer> applePositions;
	public int day;
	
	public TileEntityZapAppleLog()
	{
		DaytimeManager.registerListener( this, worldObj );
		resetValues();
	}
	
	public void resetValues()
	{
		leafPositions = Lists.newLinkedList();
		logPositions = Lists.newLinkedList();
		applePositions = Maps.newHashMap();
		finished = new boolean[6];
		delay = 5;
		rand = new Random();
		day = 0;
	}
	
	@Override
	public void onDayChange()
	{
		day++;
	}
	
	@Override
	public boolean canUpdate()
	{
		return true;
	}
	
	@Override
	@SideOnly( Side.SERVER )
	public void updateEntity()
	{
		if ( --delay <= 0 && !worldObj.isRemote )
		{
			delay = 10;
			// int day = DaytimeManager.getDay( worldObj );
			if ( day == 0 )
			{
				finished[0] = false;
				finished[1] = false;
				finished[2] = false;
				finished[3] = false;
				finished[4] = false;
				finished[5] = false;
			}
			else if ( ( day == 1 ) && ( finished[0] == false ) )
			{
				worldObj.playSoundEffect( xCoord, yCoord, zCoord, "mob.wolf.zaphowl", 1.0F, 1.0F );
				finished[0] = true;
			}
			else if ( ( day == 2 ) && ( finished[1] == false ) )
			{
				if ( leafCopy == null )
				{
					index = 0;
					leafCopy = new ArrayList<OrderedTriple>( leafPositions );
					if ( ZapApples.lightningEffect )
					{
						int x = xCoord + rand.nextInt( 21 ) - 9;
						int y = yCoord;
						int z = zCoord + rand.nextInt( 21 ) - 9;
						worldObj.addWeatherEffect( new EntityLightningBolt( worldObj, x, y, z ) );
					}
				}
				if ( index >= leafCopy.size() )
				{
					index = 0;
					leafCopy = null;
					finished[1] = true;
				}
				else
				{
					OrderedTriple pos = (OrderedTriple) leafCopy.get( index++ );
					if ( ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.air ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.snow ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.leaves ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.vine ) )
					{
						if ( ( ZapApples.lightningEffect ) && ( rand.nextInt( 30 ) == 1 ) )
						{
							int x = xCoord + rand.nextInt( 21 ) - 9;
							int y = yCoord;
							int z = zCoord + rand.nextInt( 21 ) - 9;
							worldObj.addWeatherEffect( new EntityLightningBolt( worldObj, x, y, z ) );
						}
						worldObj.setBlock( pos.getX(), pos.getY(), pos.getZ(), ZapApples.zapAppleLeaves );
					}
				}
			}
			else if ( ( day == 3 ) && ( finished[2] == false ) )
			{
				if ( appleCopy == null )
				{
					index = 0;
					appleCopy = new ArrayList<OrderedTriple>( applePositions.keySet() );
				}
				if ( index >= appleCopy.size() )
				{
					index = 0;
					appleCopy = null;
					finished[2] = true;
				}
				else
				{
					OrderedTriple pos = (OrderedTriple) appleCopy.get( index++ );
					if ( ( ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.air ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.snow ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.leaves ) || ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == Blocks.vine ) ) && ( ZapApples.zapAppleFlowers.canPlaceBlockOnSide( worldObj, pos.getX(), pos.getY(), pos.getZ(), ( (Integer) applePositions.get( pos ) ).intValue() ) ) )
					{
						worldObj.setBlock( pos.getX(), pos.getY(), pos.getZ(), ZapApples.zapAppleFlowers );
						ZapApples.zapAppleFlowers.updateBlockMetadata( worldObj, pos.getX(), pos.getY(), pos.getZ(), ( (Integer) applePositions.get( pos ) ).intValue(), 0.0F, 0.0F, 0.0F );
					}
				}
			}
			else if ( ( day == 4 ) && ( finished[3] == false ) )
			{
				if ( appleCopy == null )
				{
					index = 0;
					appleCopy = new ArrayList<OrderedTriple>( applePositions.keySet() );
				}
				if ( index >= appleCopy.size() )
				{
					index = 0;
					appleCopy = null;
					finished[3] = true;
				}
				else
				{
					OrderedTriple pos = (OrderedTriple) appleCopy.get( index++ );
					if ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == ZapApples.zapAppleFlowers )
					{
						worldObj.setBlock( pos.getX(), pos.getY(), pos.getZ(), ZapApples.grayApple );
						PacketHandler.getDispatcher().sendToDimension( new SendEffectsPacket( 1, pos.getX(), pos.getY(), pos.getZ(), ZapApples.zapAppleFlowers, 0 ), worldObj.provider.dimensionId );
						ZapApples.grayApple.updateBlockMetadata( worldObj, pos.getX(), pos.getY(), pos.getZ(), ( (Integer) applePositions.get( pos ) ).intValue(), 0.0F, 0.0F, 0.0F );
					}
				}
			}
			else if ( ( day == 5 ) && ( finished[4] == false ) )
			{
				if ( appleCopy == null )
				{
					index = 0;
					appleCopy = new ArrayList<OrderedTriple>( applePositions.keySet() );
					if ( ZapApples.lightningEffect )
					{
						int x = xCoord + rand.nextInt( 21 ) - 9;
						int y = yCoord;
						int z = zCoord + rand.nextInt( 21 ) - 9;
						worldObj.addWeatherEffect( new EntityLightningBolt( worldObj, x, y, z ) );
					}
				}
				if ( index >= appleCopy.size() )
				{
					index = 0;
					appleCopy = null;
					finished[4] = true;
				}
				else
				{
					OrderedTriple pos = (OrderedTriple) appleCopy.get( index++ );
					if ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == ZapApples.grayApple )
					{
						worldObj.setBlock( pos.getX(), pos.getY(), pos.getZ(), ZapApples.zapApple, 0, 2 );
						// PacketHandler.getDispatcher().sendToDimension( new
						// SendEffectsPacket( 1, pos.getX(), pos.getY(),
						// pos.getZ(), ZapApples.grayApple, 0 ),
						// worldObj.provider.dimensionId );
						ZapApples.zapApple.updateBlockMetadata( worldObj, pos.getX(), pos.getY(), pos.getZ(), ( (Integer) applePositions.get( pos ) ).intValue(), 0.0F, 0.0F, 0.0F );
						if ( rand.nextBoolean() )
						{
							int x = xCoord + rand.nextInt( 21 ) - 9;
							int y = yCoord;
							int z = zCoord + rand.nextInt( 21 ) - 9;
							worldObj.addWeatherEffect( new EntityLightningBolt( worldObj, x, y, z ) );
						}
					}
				}
			}
			else if ( ( day == 6 ) && ( finished[5] == false ) )
			{
				if ( leafCopy == null )
				{
					index = 0;
					leafCopy = new ArrayList<OrderedTriple>( leafPositions );
					
					ZapApples.zapApple.isHarvestDay = false;
				}
				if ( index >= leafCopy.size() )
				{
					index = 0;
					leafCopy = null;
					finished[5] = true;
				}
				else
				{
					OrderedTriple pos = (OrderedTriple) leafCopy.get( index++ );
					if ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == ZapApples.zapAppleLeaves )
					{
						ZapApples.zapAppleLeaves.removeLeaves( worldObj, pos.getX(), pos.getY(), pos.getZ() );
					}
				}
			}
			
			ZapApples.zapApple.isHarvestDay = ( day == 5 );
		}
	}
	
	public void readFromNBT( NBTTagCompound tag )
	{
		super.readFromNBT( tag );
		
		resetValues();
		index = tag.getInteger( "index" );
		day = tag.getInteger( "day" );
		
		finished[0] = tag.getBoolean( "finished[0]" );
		finished[1] = tag.getBoolean( "finished[1]" );
		finished[2] = tag.getBoolean( "finished[2]" );
		finished[3] = tag.getBoolean( "finished[3]" );
		finished[4] = tag.getBoolean( "finished[4]" );
		finished[5] = tag.getBoolean( "finished[5]" );
		
		NBTTagCompound leaves = tag.getCompoundTag( "leaves" );
		int leafSize = leaves.getInteger( "size" );
		for ( int i = 0; i < leafSize; i++ )
		{
			leafPositions.add( OrderedTriple.valueOf( leaves.getString( "" + i ) ) );
		}
		
		NBTTagCompound logs = tag.getCompoundTag( "logs" );
		int logSize = logs.getInteger( "size" );
		for ( int i = 0; i < logSize; i++ )
		{
			logPositions.add( OrderedTriple.valueOf( logs.getString( "" + i ) ) );
		}
		
		NBTTagCompound apples = tag.getCompoundTag( "apples" );
		int appleSize = apples.getInteger( "size" );
		for ( int i = 0; i < appleSize; i++ )
		{
			applePositions.put( OrderedTriple.valueOf( apples.getString( "key:" + i ) ), Integer.valueOf( apples.getInteger( "value:" + i ) ) );
		}
	}
	
	public void writeToNBT( NBTTagCompound tag )
	{
		super.writeToNBT( tag );
		
		tag.setInteger( "index", index );
		tag.setInteger( "day", day );
		
		tag.setBoolean( "finished[0]", finished[0] );
		tag.setBoolean( "finished[1]", finished[1] );
		tag.setBoolean( "finished[2]", finished[2] );
		tag.setBoolean( "finished[3]", finished[3] );
		tag.setBoolean( "finished[4]", finished[4] );
		tag.setBoolean( "finished[5]", finished[5] );
		
		NBTTagCompound leaves = new NBTTagCompound();
		leaves.setInteger( "size", leafPositions.size() );
		for ( int i = 0; i < leafPositions.size(); i++ )
		{
			leaves.setString( "" + i, ( (OrderedTriple) leafPositions.get( i ) ).toString() );
		}
		tag.setTag( "leaves", leaves );
		
		NBTTagCompound logs = new NBTTagCompound();
		logs.setInteger( "size", logPositions.size() );
		for ( int i = 0; i < logPositions.size(); i++ )
		{
			logs.setString( "" + i, ( (OrderedTriple) logPositions.get( i ) ).toString() );
		}
		tag.setTag( "logs", logs );
		
		NBTTagCompound apples = new NBTTagCompound();
		List<OrderedTriple> temp = new ArrayList<OrderedTriple>( applePositions.keySet() );
		apples.setInteger( "size", temp.size() );
		for ( int i = 0; i < temp.size(); i++ )
		{
			apples.setString( "key:" + i, ( (OrderedTriple) temp.get( i ) ).toString() );
			apples.setInteger( "value:" + i, ( (Integer) applePositions.get( temp.get( i ) ) ).intValue() );
		}
		tag.setTag( "apples", apples );
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT( tag );
		return new S35PacketUpdateTileEntity( xCoord, yCoord, zCoord, 0, tag );
	}
	
	@Override
	public void onDataPacket( NetworkManager net, S35PacketUpdateTileEntity pkt )
	{
		readFromNBT( pkt.func_148857_g() );
		markForUpdate();
	}
	
	public void markForUpdate()
	{
		worldObj.markBlockForUpdate( xCoord, yCoord, zCoord );
	}
	
	public void notifyRemoval()
	{
		DaytimeManager.unregisterListener( this );
		
		for ( OrderedTriple pos : logPositions )
		{
			int meta = worldObj.getBlockMetadata( pos.getX(), pos.getY(), pos.getZ() );
			if ( meta < 0 )
				meta = 0;
			if ( worldObj.getBlock( pos.getX(), pos.getY(), pos.getZ() ) == ZapApples.zapAppleLog )
			{
				worldObj.setBlock( pos.getX(), pos.getY(), pos.getZ(), ZapApples.zapAppleDeadLog, meta, 0 );
				worldObj.markBlockForUpdate( pos.getX(), pos.getY(), pos.getZ() );
			}
		}
	}
}
