package com.demo.elmozzo.moviebuster.bonus.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.demo.elmozzo.moviebuster.bonus.dao.BonusDAO;
import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.queue.object.RPCMessage;

/**
 * Unit tests for {@link BonusRPCServer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BonusRPCServerTest {

	/** The mocked DAO. */
	private static final BonusDAO DAO = mock(BonusDAO.class);

	/** The resource. */
	private final BonusRPCServer RESOURCE = new BonusRPCServer(null, "testQ", DAO);

	/** The bonus captor. */
	@Captor
	private ArgumentCaptor<Bonus> bonusCaptor;

	/** The bonus object. */
	private Bonus bonus;

	/** The insert req. */
	private RPCMessage insertReq;

	/**
	 * Insert bonus.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void insertBonus() throws Exception {
		when(DAO.create(any(Bonus.class))).thenReturn(this.bonus);

		final String response = this.RESOURCE.getValue(this.insertReq.toString());
		assertThat(response).isNotNull();

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isEqualTo(this.bonus.toString());
		assertThat(Bonus.fromString(responseObj.getObj())).isEqualTo(this.bonus);

		verify(DAO).create(this.bonusCaptor.capture());
		assertThat(this.bonusCaptor.getValue()).isEqualTo(this.bonus);
	}

	/**
	 * Setup bonus obj.
	 */
	@Before
	public void setup() {
		this.bonus = new Bonus();
		this.bonus.setId(1L);
		this.bonus.setBonusQuantity(1);
		this.bonus.setCustomerId(1);

		this.insertReq = new RPCMessage("create", null, this.bonus.toString());
	}

	/**
	 * Tear down after.
	 */
	@After
	public void tearDown() {
		reset(DAO);
	}

}
