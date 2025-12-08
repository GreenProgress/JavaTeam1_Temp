package TestPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays; 
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private SituationRepository situationRepository;

    @Override
    public void run(String... args) throws Exception {
    	Scanner scanner = new Scanner(System.in);
        
        while(true) { 
            
            System.out.println("\n======================================");
        	System.out.println("[테스트할 항목을 선택하세요.]");
        	System.out.println("1. MongoDB 연결 테스트(상황 하나 추가)");
        	System.out.println("2. 데이터 삭제 테스트(데이터 모두 삭제)");
            System.out.println("3. 초기화 및 샘플 데이터 10개 추가 (임대차, 노동, 소비자)");
            System.out.println("4. 추가할 데이터 직접 입력");
            System.out.println("5. ID로 데이터 삭제");
            System.out.println("6. 전체 데이터 조회");
            System.out.println("7. 프로그램 종료");
        	System.out.print("select: ");
        	int selectTest = scanner.nextInt();
            
            scanner.nextLine(); // 버퍼에 있는 \n를 제거
            
        	switch(selectTest) {
        	case 1:
        		System.out.println("--- MongoDB 연결 테스트 시작 ---");
                Situation newSituation = new Situation();
                newSituation.setTitle("MongoDB 연결 테스트로 들어간 샘플입니다");
                newSituation.setCategory("테스트 분류"); 
                newSituation.setActive(true);
                newSituation.setUpdatedAt(new Date());
                situationRepository.save(newSituation);
                System.out.println(">> 데이터 저장 성공!");
                List<Situation> situations = situationRepository.findAll();
                System.out.println(">> 저장된 데이터 전체 조회:");
                for (Situation situation : situations) {
                    System.out.println(situation.toString());
                }
                System.out.println("--- MongoDB 연결 테스트 종료 ---");
        		break;
        	case 2:
        		System.out.println("--- 데이터 삭제 테스트 시작 ---");
        	    System.out.println("\n[1] 모든 데이터 삭제 (초기화)");
        	    situationRepository.deleteAll();
        	    System.out.println("\n[2] 테스트용 데이터 3개 저장");
        	    situationRepository.save(new Situation("아르바이트 임금 체불", "노동"));
        	    Situation contractSituation = situationRepository.save(new Situation("전세 계약 시 주의사항", "임대차"));
        	    situationRepository.save(new Situation("중고 거래 사기 대처법", "소비자"));
        	    System.out.println(">> 저장 후 전체 데이터:");
                List<Situation> list2_1 = situationRepository.findAll();
                for(Situation s : list2_1) { System.out.println(s); }
        	    System.out.println("\n[3] '전세 계약' 데이터를 ID로 삭제");
        	    String idToDelete = contractSituation.getId();
        	    situationRepository.deleteById(idToDelete);
        	    System.out.println(">> ID로 삭제 후 전체 데이터:");
                List<Situation> list2_2 = situationRepository.findAll();
                for(Situation s : list2_2) { System.out.println(s); }
        	    System.out.println("\n[4] '중고 거래 사기 대처법' 데이터를 Title로 삭제");
        	    long deletedCount = situationRepository.deleteByTitle("중고 거래 사기 대처법");
        	    System.out.println(">> Title로 삭제된 문서 개수: " + deletedCount);
        	    System.out.println(">> Title로 삭제 후 전체 데이터:");
                List<Situation> list2_3 = situationRepository.findAll();
                for(Situation s : list2_3) { System.out.println(s); }
        	    System.out.println("\n[5] 남은 데이터 모두 삭제");
        	    situationRepository.deleteAll();
        	    System.out.println(">> 최종 전체 데이터:");
        	    List<Situation> finalList = situationRepository.findAll();
        	    if (finalList.isEmpty()) {
        	        System.out.println(">> 모든 데이터가 성공적으로 삭제되었습니다.");
        	    }
        	    System.out.println("\n--- 데이터 삭제 테스트 종료 ---");
        		break;

            case 3:
                System.out.println("--- 샘플 데이터 10개 추가 시작 ---");
                System.out.println("[1] 기존 Situation 컬렉션 데이터 삭제 (초기화)");
                situationRepository.deleteAll();
                System.out.println("[2] 샘플 데이터 10개 생성");
                List<Situation> sampleSituations = Arrays.asList(
                    new Situation("아르바이트 임금 체불", "노동"),
                    new Situation("부당 해고를 당했습니다", "노동"),
                    new Situation("근로계약서를 작성하지 않았어요", "노동"),
                    new Situation("최저시급을 받지 못했어요", "노동"),
                    new Situation("전세 계약 시 주의사항", "임대차"),
                    new Situation("월세 보증금 반환 문제", "임대차"),
                    new Situation("임대인이 수리를 안 해줘요 (누수, 파손)", "임대차"),
                    new Situation("중고 거래 사기 대처법", "소비자"),
                    new Situation("구매한 물건 환불/교환 거부", "소비자"),
                    new Situation("배달 앱에서 이물질이 나왔어요", "소비자")
                );
                situationRepository.saveAll(sampleSituations);
                System.out.println("[3] 데이터베이스에 10개 데이터 저장 완료!");
                System.out.println("\n>> 저장된 Situation 데이터 전체 목록:");
                List<Situation> list3 = situationRepository.findAll();
                for(Situation s : list3) { System.out.println(s); }
                System.out.println("\n--- 샘플 데이터 추가 종료 ---");
                break;
            case 4:
            	System.out.println("추가할 상황의 카테고리를 선택하세요.\n1.임대차\t2.노동\t3.소비자");
            	System.out.print("선택: ");
            	String tmpCateg = null, tmpSituation = null;
            	int select = scanner.nextInt();
            	scanner.nextLine(); // 버퍼에 있는 \n를 제거
            	switch(select) 
            	{
            	case 1: 
            		tmpCateg = "임대차";
            		System.out.print("추가할 상황을 입력하세요: ");
            		tmpSituation = scanner.nextLine();
            		break;
            	case 2:
            		tmpCateg = "노동";
            		System.out.print("추가할 상황을 입력하세요: ");
            		tmpSituation = scanner.nextLine();
            		break;
            	case 3:
            		tmpCateg = "소비자";
            		System.out.print("추가할 상황을 입력하세요: ");
            		tmpSituation = scanner.nextLine();
            		break;
            	default:
                    System.out.println("잘못된 선택입니다.");
            		break;
            	}
                
            	if(tmpCateg != null && tmpSituation != null && !tmpSituation.isBlank())
            	{
            		Situation newSituation2 = new Situation();
                    newSituation2.setTitle(tmpSituation);
                    newSituation2.setCategory(tmpCateg);
                    newSituation2.setActive(true);
                    newSituation2.setUpdatedAt(new Date());
                    situationRepository.save(newSituation2);
                    System.out.println(">> [저장 성공] " + newSituation2.toString());
            	}
            	else {
                    System.out.println(">> 입력이 취소되었거나 유효하지 않아 저장되지 않았습니다.");
                }
            	
            	System.out.println("\n>> 저장된 Situation 데이터 전체 목록:");
                List<Situation> list4 = situationRepository.findAll();
                for(Situation s : list4) { System.out.println(s); }
                break;

            case 5:
                System.out.println("--- ID로 데이터 삭제 시작 ---");
                System.out.println("\n>> 현재 Situation 데이터 전체 목록:");
                List<Situation> currentList = situationRepository.findAll();
                if (currentList.isEmpty()) {
                    System.out.println(">> 데이터가 없습니다. 삭제할 수 없습니다.");
                } else {
                    for (Situation s : currentList) {
                        System.out.println(s);
                    }
                    System.out.print("\n삭제할 데이터의 ID를 입력하세요: ");
                    String idToDeleteC5 = scanner.nextLine();
                    if (idToDeleteC5 == null || idToDeleteC5.isBlank()) {
                        System.out.println(">> ID가 입력되지 않았습니다. 취소합니다.");
                    } 
                    else if (situationRepository.existsById(idToDeleteC5)) {
                        situationRepository.deleteById(idToDeleteC5);
                        System.out.println(">> ID [" + idToDeleteC5 + "] 데이터 삭제 성공.");
                    } else {
                        System.out.println(">> 해당 ID [" + idToDeleteC5 + "]가 존재하지 않습니다.");
                    }
                    System.out.println("\n>> 삭제 후 Situation 데이터 전체 목록:");
                    List<Situation> list5_after = situationRepository.findAll();
                    if (list5_after.isEmpty()) {
                        System.out.println(">> 모든 데이터가 삭제되었습니다.");
                    } else {
                        for (Situation s : list5_after) {
                            System.out.println(s);
                        }
                    }
                }
                System.out.println("\n--- ID로 데이터 삭제 종료 ---");
                break;
            case 6:
                System.out.println("--- 전체 데이터 조회 시작 ---");
                System.out.println("\n>> 저장된 Situation 데이터 전체 목록:");
                List<Situation> allSituations = situationRepository.findAll();
                
                if (allSituations.isEmpty()) {
                    System.out.println(">> 데이터가 없습니다.");
                } else {
                    for (Situation s : allSituations) {
                        System.out.println(s);
                    }
                }
                System.out.println("\n--- 전체 데이터 조회 종료 ---");
                break;
            
            case 7:
                System.out.println("--- 프로그램을 종료합니다 ---");
                scanner.close(); // 2. Scanner 리소스를 닫음
                System.exit(0);  // 3. Spring Boot 애플리케이션(JVM)을 강제 종료
                break;

        	default:
        		System.out.println("잘못된 선택입니다. (1~7 사이의 숫자 입력)");
        		break;
        	} 
        }
    }
}