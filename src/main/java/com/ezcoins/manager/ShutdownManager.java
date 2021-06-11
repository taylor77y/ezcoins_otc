package com.ezcoins.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * 确保应用退出时能关闭后台线程
 * 
 */
@Component
public class ShutdownManager {
    private static final Logger logger = LoggerFactory.getLogger("sys-user");

    @PreDestroy
    public void destroy() {
        shutdownAsyncManager();
    }

    @PostConstruct
    public void initialize() {
        logger.info("初始化...");
//        List<UserTeam> userTeams = SpringUtils.getBean(UserTeamMapper.class).selectUserTeamList();
//       for (UserTeam userTeam:userTeams){
//            for (int i = 0; i < 7; i++) {
//                if (i==2||i==3||i==4||i==5||i==6){
//                    SpringUtils.getBean(AchievementService.class).finishAchievement(userTeam.getUserId(),i,0L);
//                }
//            }
//        }
        try {
            // SpringUtils.getBean(ICoinWalletService.class).selectCoinWalletByCoinIdListToUdun(1L,1001L);
            // SpringUtils.getBean(ICoinWalletService.class).handleRecharge(null,
            // "2868cc39ce22466b86ff8b139916ffec0051095b9ab7deaef79dcc1e03752c62", null,
            // null, null, null, null);
//            CoinRecord cr = new CoinRecord();
//            cr.setUserId(1001L);
//            cr.setCoinId(1L);
//            cr.setToAddress("TEa9skLsu5DJyEmyhghSnTG32n1Z3rns6z");
//            cr.setCoinName("trc20");
//            cr.setAmount(new BigDecimal(10));
//            SpringUtils.getBean(ICoinWalletService.class).withdraw(cr);
//            SpringUtils.getBean(IHotalBuyroomRecordService.class).buyRoom(1001L,1);
//            System.out.println("记录"+SpringUtils.getBean(IHotalBuyroomRecordService.class).selectHotalBuyroomRecordByUserIdAndThisMonth(1001L));
//            List<HotalTaskConfig> list = SpringUtils.getBean(IHotalTaskConfigService.class).selectHotalTaskConfigList(null);
//            for (HotalTaskConfig hotalTaskConfig : list) {
//                hotalTaskConfig.setTaskName(null);
//            }
//            for (HotalTaskConfig hotalTaskConfig : list) {
//                System.out.println("Name:"+hotalTaskConfig.getTaskName());
//            }
//            List<UserRevenueVO> list = SpringUtils.getBean(IHotalAccService.class).userRevenueList(6);
//            for (UserRevenueVO vo : list) {
//                System.out.println("date"+vo.getDayDate() +"|"+ vo.getRevenue());
//            }
//            SpringUtils.getBean(IHotalAccService.class).upHotelStar();
//            SpringUtils.getBean(IHotalAccService.class).calculateRevenue();
//            SpringUtils.getBean(IHotalShareTotalService.class).monthShare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void shutdownAsyncManager() {
        try {
            logger.info("====关闭后台任务任务线程池====");
            AsyncManager.me().shutdown();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
