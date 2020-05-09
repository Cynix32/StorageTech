package javapower.storagetech.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.blaze3d.systems.RenderSystem;

import javapower.storagetech.container.ContainerFluidDiskWorkbench;
import javapower.storagetech.core.ClientConfig;
import javapower.storagetech.core.ClientSetup;
import javapower.storagetech.core.CommonConfig;
import javapower.storagetech.core.PacketCreateDisk;
import javapower.storagetech.core.ResourceLocationRegister;
import javapower.storagetech.core.StorageTech;
import javapower.storagetech.util.Tools;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class ScreenContainerFluidDiskWorkbench extends ContainerScreen<ContainerFluidDiskWorkbench>
{
	TextFieldWidget textField_size;
	
	int disk_size = 1000;
	private String[] i18nBuffer;
	int slot = -1;
	int animaite = 0;
	long energycost = 0;
	//28 17
	Button button_create = new Button(28, 17, 40, 20, I18n.format("storagetech.gui.create"), (button) ->
	{
		//TODO send to server the starting
		StorageTech.INSTANCE_CHANNEL.sendToServer(new PacketCreateDisk(container.tile.getPos(), disk_size));
	});
	
	public ScreenContainerFluidDiskWorkbench(ContainerFluidDiskWorkbench _screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(_screenContainer, inv, titleIn);
		
		ClientConfig.loadConfig();
		
	    i18nBuffer = new String[]
				{
					I18n.format("storagetech.gui.creation"),
					I18n.format("storagetech.gui.availablespace"),
					I18n.format("storagetech.gui.disksizein"),
					I18n.format("storagetech.gui.info"),
					I18n.format("storagetech.gui.insertfluidstoragepart"),
					I18n.format("storagetech.gui.insertstoragehousing"),
					I18n.format("storagetech.gui.for"),
					I18n.format("storagetech.gui.cost")
				};
	    
	    //addButton(button_create);
	    //addButton(textField_size);
	    
	    this.passEvents = false;
	}
	
	@Override
	public void init()
	{
		this.xSize = 176;
	    this.ySize = 189;
	    
		super.init();
	    
		textField_size = new TextFieldWidget(font, 28, 17, 70, 20,""+disk_size);
		textField_size.setValidator(new Predicate<String>()
		{
			
			@Override
			public boolean test(String text)
			{
				if(text.length() == 0)
				{
					disk_size = 1;
					return true;
				}
				
				boolean invalid = false;
				try
				{
					double disk_size_d = Double.parseDouble(text);
					if(disk_size_d > CommonConfig.Value_DiskFluidMaxSize)
						invalid = true;
					else
						disk_size = (int) disk_size_d;
				}
				catch (Exception e)
				{
					return false;
				}
				
				if(invalid)
				{
					disk_size = CommonConfig.Value_DiskFluidMaxSize;
					textField_size.setText(""+disk_size);
					return false;
				}
				
				energycost = ((long)CommonConfig.Value_EnergyCostPerSize)*disk_size;
				
				//System.out.println(t);
				return true;
			}
		});
		textField_size.setMaxStringLength(10);
		textField_size.setText(""+disk_size);
		//button_create.x = 40;
		//addButton(button_create);
	    addButton(textField_size);
	    addButton(button_create);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		
	    this.renderBackground();
	    super.render(mouseX, mouseY, partialTicks);
	    this.renderHoveredToolTip(mouseX, mouseY);
	    
	    if(ClientConfig.Value_showHelp)
		{
			if(slot == 0)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add("�b(i) �f"+i18nBuffer[3]);
	        	list.add("�7"+i18nBuffer[4]);
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, minecraft.currentScreen.width, minecraft.currentScreen.height, 150, font);
	        }
			else if(slot == 1)
			{
				List<String> list = new ArrayList<String>();
	        	list.add("�b(i) �f"+i18nBuffer[3]);
	        	list.add("�7"+i18nBuffer[5]);
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, minecraft.currentScreen.width, minecraft.currentScreen.height, 150, font);
			}
		}
	 }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
        button_create.x = 100 + guiLeft;
        button_create.y = 65 + guiTop;
        
        textField_size.x = 28 + guiLeft;
        textField_size.y = 65 + guiTop;
        
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	    minecraft.getTextureManager().bindTexture(ResourceLocationRegister.gui_fdiskwb);
	    
	    blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	    blit(guiLeft-24, guiTop+10, 210, 0, 27, 82);
	    
	    if(CommonConfig.Value_EnableCostDisk)
        {
	        this.blit(guiLeft+173, guiTop, 176, 0, 22, 98);
	        
	        if(container.tile.energyBuffer.energy > 0)
	        {
	        	int yw = (int)(59*(container.tile.energyBuffer.energy/(float)container.tile.energyBuffer.capacity));
	        	this.blit(guiLeft+176, guiTop+20+(59-yw), 198, 59-yw, 12, yw);
	        }
	        
	        if(container.tile.prosses && prossesTime() > 0.0f)
	        	this.blit(guiLeft+148, guiTop+36, 176, 98, 15, Math.min((int)(22*prossesTime()),22));
        }
	    
	    if(container.tile.prosses && prossesTime() > 0.0f)
        {
        	blit(guiLeft+26, guiTop+15, 0, 194, 121, 18);
        	blit(guiLeft+27, guiTop+16, 0 + animaite, 189, 114, 5);
        	blit(guiLeft+27, guiTop+48, 0 + animaite, 189, 114, 5);
        	
        	 this.drawCenteredString(font, i18nBuffer[0]+" "+(((int)(prossesTime()*10000))/100f)+" %", guiLeft + 84, guiTop + 30, 0xffffff);
	        if(animaite < 0)
	        	animaite = 10;
	        else
	        	--animaite;
        }
        else
        {
        
	        this.drawString(font, i18nBuffer[1]+": ", guiLeft + 28, guiTop + 6, 0xffffff);
	        if (container.tile.memory < 1000_000_000_000l)
        	{
        		String stringshow = ClientSetup.formatter.format(container.tile.memory);
        		String[] parts = stringshow.split(",");
        		if(parts != null)
        		{
	        		int dec = 5 - parts.length;
	        		for(String word : parts)
	        		{
	        			this.drawString(font, word, guiLeft + 28 + 22*dec, guiTop + 17, 0xffffff);
	        			++dec;
	        		}
        		}
        	}
	        else
	        {
	        	int size = ((""+container.tile.memory).length()-9)/3;
	        	
	        	String pre = ""+ "kMGTPE?".charAt(size < 6 && size > 0 ? size-1 : 6);
	        	String stringshow = ClientSetup.formatter.format(container.tile.memory/(long) Math.pow(10, size*3)) + " "+pre+"*VFT";
	        	String[] parts = stringshow.split(" ");
        		if(parts != null)
        		{
	        		int dec = 5 - parts.length;
	        		for(String word : parts)
	        		{
	        			if(word.contains("*"))
	        				word = word.replace('*', ' ');
	        			this.drawString(font, word, guiLeft + 28 + 22*dec, guiTop + 17, 0xffffff);
	        			++dec;
	        		}
        		}
	        }
	        this.drawString(font, i18nBuffer[2]+" VFT:", guiLeft + 28, guiTop + 47, 0xffffff);
        
        }
	    
	    //textField_size.render(mouseX, mouseY, partialTicks);//TODO
	    
	    this.drawString(font, "VFT: Virtual Fluid Tank", guiLeft + 34, guiTop + 87, 0xffffff);
        this.drawString(font, "1 VFT = "+i18nBuffer[6]+" 1 mB", guiLeft + 40, guiTop + 97, 0xffffff);
        
        if(CommonConfig.Value_EnableCostDisk)
        {
	        if(container.tile.prosses && mouseX >= guiLeft+148 && mouseY >= guiTop+36 && mouseX <= guiLeft+163 && mouseY <= guiTop+58)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add((((int)(prossesTime()*10000))/100f)+" %");
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, minecraft.currentScreen.width, minecraft.currentScreen.height, 100, font);
	        }
	        
	        if(mouseX >= guiLeft+176 && mouseY >= guiTop+20 && mouseX <= guiLeft+187 && mouseY <= guiTop+79)
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add(ClientSetup.formatter.format(container.tile.energyBuffer.energy)+" RF /"+ClientSetup.formatter.format(container.tile.energyBuffer.capacity)+" RF");
	        	GuiUtils.drawHoveringText(list, mouseX, mouseY, minecraft.currentScreen.width, minecraft.currentScreen.height, 200, font);
	        }
	        
	        if(textField_size.isFocused())
	        {
	        	List<String> list = new ArrayList<String>();
	        	list.add(i18nBuffer[7]+": "+Tools.longFormatToString(energycost)+" RF");
	        	GuiUtils.drawHoveringText(list, guiLeft, guiTop, minecraft.currentScreen.width, minecraft.currentScreen.height, 200, font);
	        }
        }
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		textField_size.tick();
		
		Slot s = getSlotUnderMouse();
		if(s != null && s.getStack().isEmpty())
		{
			if(s.slotNumber == 36)//storage part
			{
				slot = 0;
			}
			else if(s.slotNumber == 37)//storage Housing
			{
				slot = 1;
			}
			else
			{
				slot = -1;
			}
		}
		else
			slot = -1;
		
		if(CommonConfig.Value_EnableCostDisk)
			if(container.tile.prosses)
			{
				if(button_create.active)
					button_create.active = false;
			}
			else
			{
				if(!button_create.active)
					button_create.active = true;
			}
	}
	
	/*@Override
	public boolean mouseClicked(double xm, double ym, int mb)
	{
		textField_size.mouseClicked(xm, ym, mb);
		
		return super.mouseClicked(xm, ym, mb);
	}*/
	
	/*@Override
	public boolean keyPressed(int typedChar, int keyCode, int p_keyPressed_3_)
	{
		if("0123456789".indexOf((char)typedChar) != -1 || keyCode == 14 || keyCode == 203 || keyCode == 205)
		{
			textField_size.keyPressed(typedChar, keyCode, p_keyPressed_3_);
			updateTFInteger();
		}
		return super.keyPressed(typedChar, keyCode, p_keyPressed_3_);
	}*/
	
	@SuppressWarnings("unused")
	private void updateTFInteger()
	{
		String text = textField_size.getText();
		boolean invalid = false;
		try
		{
			double disk_size_d = Double.parseDouble(text);
			if(disk_size_d > CommonConfig.Value_DiskFluidMaxSize)
				invalid = true;
			else
				disk_size = (int) disk_size_d;
		}
		catch (Exception e)
		{
			
		}
		
		if(invalid)
		{
			disk_size = CommonConfig.Value_DiskFluidMaxSize;
			textField_size.setText(""+disk_size);
		}
		
		energycost = ((long)CommonConfig.Value_EnergyCostPerSize)*disk_size;
	}
	
	private float prossesTime()
	{
		return ((float)container.tile.createProsses)/container.tile.diskSize;
	}

}