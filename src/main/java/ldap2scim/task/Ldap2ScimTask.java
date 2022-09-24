package ldap2scim.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import ldap2scim.common.CommonConstants;
import ldap2scim.model.TaskRecord;
import ldap2scim.service.LdapService;
import ldap2scim.utils.UUIDUtils;

@Component
public class Ldap2ScimTask implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(Ldap2ScimTask.class);

	public static List<TaskRecord> taskRecords = new ArrayList<>();

	@Override
	public void run(String... args) throws Exception {

		try {

			if (!CommonConstants.SCIM_SYNC_CRON_ENABLED) {
				logger.info("[task] scim sync task disabled");
				return;
			}

			if (StringUtils.isBlank(CommonConstants.SCIM_SYNC_CRON_EXPRESSION)) {
				logger.info("[task] scim sync expression is blank!");
				return;
			}

			CronExpression cronTrigger = CronExpression.parse(CommonConstants.SCIM_SYNC_CRON_EXPRESSION);
			logger.info("[task] scim sync task enabled:{}", CommonConstants.SCIM_SYNC_CRON_EXPRESSION);
			logger.info("[task] next execute time:{}",
					cronTrigger.next(LocalDateTime.now()).format(CommonConstants.DateTimeformatter));
			ConcurrentTaskScheduler executor = new ConcurrentTaskScheduler();
			ScheduledFuture<?> future = executor.schedule(new Runnable() {

				@Override
				public void run() {
					try {
						TaskRecord taskRecord = new TaskRecord();
						String uuid = UUIDUtils.generateUUID();
						taskRecord.setUuid("cron_" + uuid);
						taskRecord.setExecuteTime(LocalDateTime.now().format(CommonConstants.DateTimeformatter));
						logger.info("[task][{}] scim sync start!", uuid);
						List<Map<String, String>> ldapList = LdapService.searchLdapUser(CommonConstants.LDAP_Searchbase,
								CommonConstants.LDAP_Searchfilter);
						String result = LdapService.syncLdaptoScim(ldapList);
						logger.info("[task][{}] scim sync end!", uuid);
						taskRecord.setResult(result);
						taskRecords.add(taskRecord);
						if (taskRecords.size() >= 200) {
							taskRecords.remove(0);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}, new CronTrigger(CommonConstants.SCIM_SYNC_CRON_EXPRESSION));
			// future.cancel(true);
			logger.info("[task] future:{}", future.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public static void main(String[] args) {
		CronExpression cronTrigger = CronExpression.parse("0 15 3 * * *");
		System.out.println(cronTrigger.next(LocalDateTime.now()).format(CommonConstants.DateTimeformatter));
	}

}
