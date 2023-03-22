package foundation.servlet.web.frontcontroller.v2;



import foundation.servlet.web.frontcontroller.MyView;
import foundation.servlet.web.frontcontroller.v2.controller.*;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//v2의 핵심은        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
//                  dispatcher.forward(request,response);
//중복 코드를 하나로 줄임
@WebServlet(name = "frontControllerServletV2",urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {
    private Map<String, ControllerV2> controllerMap = new HashMap<>();

    public FrontControllerServletV2() { //생성자 컨트롤러 매핑 처리
        controllerMap.put("/front-controller/v2/members/new-form",new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save",new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members",new MemberListControllerV2());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        ControllerV2 controller = controllerMap.get(requestURI); //다형성 부모가 자식을 담을 수 있다.
        if(controller == null){ // 예외처리 컨트롤러 매핑정보가 없으면 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyView view = controller.process(request, response);
        //v2의 핵심은 공통 코드(view rander)를 줄임
        view.render(request,response);
    }
}
