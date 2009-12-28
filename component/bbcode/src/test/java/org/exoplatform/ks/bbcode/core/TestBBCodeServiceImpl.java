/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ks.bbcode.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;

import org.exoplatform.ks.bbcode.api.BBCode;
import org.exoplatform.ks.bbcode.spi.BBCodeData;
import org.exoplatform.ks.bbcode.spi.BBCodePlugin;
import org.exoplatform.ks.common.jcr.KSDataLocation;
import org.exoplatform.ks.test.AssertUtils;
import org.exoplatform.ks.test.ConfigurationUnit;
import org.exoplatform.ks.test.ConfiguredBy;
import org.exoplatform.ks.test.ContainerScope;
import org.exoplatform.ks.test.jcr.AbstractJCRTestCase;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
@ConfiguredBy({@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/jcr/jcr-configuration.xml"), 
  @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/jcr/bbcodes-configuration.xml")})
public class TestBBCodeServiceImpl extends AbstractJCRTestCase {

  private BBCodeServiceImpl bbcodeService;
  private String bbcodesPath;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    bbcodeService = new BBCodeServiceImpl();
    KSDataLocation locator = new KSDataLocation(getRepository(), getWorkspace());
    bbcodeService.setDataLocator(locator);
    
    bbcodesPath = bbcodeService.getDataLocator().getBBCodesLocation();
    addNode(bbcodesPath, BBCodeServiceImpl.BBCODE_HOME_NODE_TYPE); // create node /bbcodes
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    deleteNode(bbcodesPath); // create node /bbcodes
  }

  public void testRegisterBBCodePlugin() throws Exception {
    BBCodePlugin plugin = new BBCodePlugin();
    plugin.setName("plugin1");
    bbcodeService.registerBBCodePlugin(plugin);
    List<BBCodePlugin> plugins = bbcodeService.getPlugins();
    assertEquals(1, plugins.size());
    assertEquals("plugin1", plugins.get(0).getName());
    
    
    // registerPlugin() adds elements (does not replace)
    BBCodePlugin plugin2 = new BBCodePlugin();
    plugin2.setName("plugin2");
    bbcodeService.registerBBCodePlugin(plugin2);
    List<BBCodePlugin> plugins2 = bbcodeService.getPlugins();    
    assertEquals("BBCode plugins list size was not incremented", 2, plugins2.size()); 
    assertEquals("plugin2", plugins.get(1).getName());
  }

  public void testInitDefaultBBCodes() throws Exception {
    
    BBCodePlugin plugin = new BBCodePlugin();
    plugin.setBbcodeData(Arrays.asList(new BBCodeData("foo", "bar", false, false)));
    bbcodeService.registerBBCodePlugin(plugin);
    bbcodeService.initDefaultBBCodes();
    String targetPath = bbcodesPath + "/"+"foo";
    assertNodeExists(targetPath);
    
  }

  public void testSave() throws Exception {
    List<BBCode> bbcodes = Arrays.asList(createBBCode("foo", "replacement", "description", "example", false, false));
    bbcodeService.save(bbcodes);
    String targetPath = bbcodesPath + "/"+"foo";
    assertNodeExists(targetPath);
    Node n = getNode(targetPath);
    assertEquals("foo", n.getProperty("exo:tagName").getString());
    assertEquals("replacement", n.getProperty("exo:replacement").getString());
    assertEquals("description", n.getProperty("exo:description").getString());
    assertEquals("example", n.getProperty("exo:example").getString());
    assertEquals(false, n.getProperty("exo:isOption").getBoolean());
    assertEquals(false, n.getProperty("exo:isActive").getBoolean());
  }
  
  public void testGetAll() throws Exception {
    List<BBCode> bbcodes = new ArrayList<BBCode>();
    bbcodes.add(createBBCode("foo", "replacement", "description", "example", false, true));
    bbcodes.add(createBBCode("foo", "replacement", "description", "example", true, false));
    bbcodes.add(createBBCode("foo2", "replacement", "description", "example", false, true));
    bbcodes.add(createBBCode("foo3", "replacement", "description", "example", false, true));
    bbcodes.add(createBBCode("foo4", "replacement", "description", "example", false, true));
    bbcodeService.save(bbcodes);
    List<BBCode> actual = bbcodeService.getAll();
    assertEquals(bbcodes.size(), actual.size());
  }

  public void testGetActive() throws Exception {
    List<BBCode> bbcodes = new ArrayList<BBCode>();
    bbcodes.add(createBBCode("foo", "replacement", "description", "example", false, true));
    bbcodes.add(createBBCode("foo", "replacement", "description", "example", true, false));
    bbcodes.add(createBBCode("foo2", "replacement", "description", "example", true, true));
    bbcodes.add(createBBCode("foo3", "replacement", "description", "example", false, true));
    bbcodes.add(createBBCode("foo4", "replacement", "description", "example", false, true));
    bbcodeService.save(bbcodes);
    List<String> actual = bbcodeService.getActive();
    assertEquals(bbcodes.size()-1, actual.size());
    AssertUtils.assertContains(actual, "foo", "foo2=", "foo3", "foo4");
  }

  public void testFindById() throws Exception {
    List<BBCode> bbcodes = Arrays.asList(createBBCode("foo", "replacement", "description", "example", true, true));
    bbcodeService.save(bbcodes);
    assertNodeExists(bbcodesPath + "/"+"foo=");
    BBCode actual = bbcodeService.findById("foo=");
    assertNotNull(actual);
    assertEquals("foo", actual.getTagName());
    assertEquals("replacement", actual.getReplacement());
    assertEquals("description", actual.getDescription());
    assertEquals("example", actual.getExample());
    assertEquals(true, actual.isOption());
    assertEquals(true, actual.isActive());
  }

  public void testDelete() throws Exception {
    List<BBCode> bbcodes = Arrays.asList(createBBCode("foo", "replacement", "description", "example", false, false));
    bbcodeService.save(bbcodes);
    String targetPath = bbcodesPath + "/"+"foo";
    assertNodeExists(targetPath);
    bbcodeService.delete("foo");
    assertNodeNotExists(targetPath);
  }

  private BBCode createBBCode(String tag, String replacement, String description, String example, boolean option, boolean active) {
    BBCode bbc = new BBCode();
    bbc.setTagName(tag);
    bbc.setReplacement(replacement);
    bbc.setDescription(description);
    bbc.setExample(example);
    bbc.setOption(option);
    bbc.setActive(active);
    return bbc;
  }
  
  
}