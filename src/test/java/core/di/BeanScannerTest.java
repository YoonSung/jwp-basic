package core.di;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanScannerTest {
	private BeanScanner cf;

	@Before
	public void setup() {
		cf = new BeanScanner("core.nmvc");
	}
	
	@Test
	public void scan() throws Exception {
		Assert.assertEquals(cf.scan().size(), 1);
	}
}
