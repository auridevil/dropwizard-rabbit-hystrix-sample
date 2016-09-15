package com.demo.elmozzo.queue;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.demo.elmozzo.queue.RabbitMQService;

/**
 * Unit tests for {@link RabbitMQService}.
 */
@RunWith(MockitoJUnitRunner.class)

public class RabbitMQServiceTest {

	/**
	 * Test singleton.
	 */
	@Test
	public void testSingleton() {
		final RabbitMQService mq1 = RabbitMQService.getInstance(null);
		final RabbitMQService mq2 = RabbitMQService.getInstance(null);
		assertThat(mq1).isEqualTo(mq2);
	}

}
