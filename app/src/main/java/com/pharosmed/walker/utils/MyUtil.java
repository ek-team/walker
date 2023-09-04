package com.pharosmed.walker.utils;

import com.pharosmed.walker.database.TrainPlanManager;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
public class MyUtil {
    public static int getDiagnosticNum(String diag){
        switch (diag){
            case "请选择":
                return 0;
            case "全髋关节置换":
                return 1;
            case "全膝关节置换":
                return 2;
            case "股骨颈骨折":
                return 3;
            case "股骨转子间骨折":
                return 4;
            case "胫骨平台骨折（钢板固定）":
                return 5;
            case "胫骨平台骨折（钢板内固定）":
                return 6;
            case "胫骨中段骨折（石膏固定）":
                return 7;
            case "胫骨中段骨折（髓内钉）":
                return 8;
            case "胫骨中段骨折（桥接钢板）":
                return 9;
            case "胫骨远端骨折":
                return 10;
            case "踝关节骨折（钢板内固定）":
                return 11;
            case "跟骨骨折（钢板固定）":
                return 12;
            case "踝关节韧带损伤（踝关节韧带重建术）":
                return 13;
            case "股骨头坏死（腓骨移植术）":
                return 14;
            case "其他":
                return 15;
            default:
                return 16;

        }
    }
    public static void insertTemplate(int saveResult){
        int diagType = getDiagnosticNum(SPHelper.getUser().getDiagnosis());
        switch (diagType){
            case 0:
                break;
            case 1:
                TrainPlanManager.getInstance().insertList1(saveResult);
                break;
            case 2:
                TrainPlanManager.getInstance().insertList2(saveResult);
                break;
            case 3:
            case 4:
                TrainPlanManager.getInstance().insertList3(saveResult);
                break;
            case 5:
                TrainPlanManager.getInstance().insertList4(saveResult);
                break;
            case 6:
                TrainPlanManager.getInstance().insertList5(saveResult);
                break;
            case 7:
                TrainPlanManager.getInstance().insertList6(saveResult);
                break;
            case 8:
            case 9:
                TrainPlanManager.getInstance().insertList7(saveResult);
                break;
            case 10:
                break;
            case 11:
                TrainPlanManager.getInstance().insertList8(saveResult);
                break;
            case 12:
                TrainPlanManager.getInstance().insertList9(saveResult);
                break;
            case 13:
                TrainPlanManager.getInstance().insertList10(saveResult);
                break;
            case 14:
                TrainPlanManager.getInstance().insertList11(saveResult);
                break;
        }
    }
    public static String getPlanSummar(){
        int diagType = getDiagnosticNum(SPHelper.getUser().getDiagnosis());
        switch (diagType){
            case 1:
            case 2:
                return "术后第一天开始负重，逐步负重,6 周内达完全负重";
            case 3:
            case 4:
                return "1周时为健侧 51%，逐步增加,12周时为健侧 87%，直至 100%";
            case 5:
                return "6周时20kg，逐步增加，16周左右达到检测100%";
            case 6:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期39周";
            case 7:
            case 8:
            case 9:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期24周";
            case 11:
                return "术后两天开始训练，逐渐 16 周后达到完全负重";
            case 12:
                return "6周内10kg，8周20kg，10周40kg，直至完全负重";
            case 13:
                return "5公斤开始，逐步负重直至完全负重";
            case 14:
                return "术后七周达到12公斤，每2周增加5公斤，直到完全负重";
            default:
                return "无";
        }
    }
}
