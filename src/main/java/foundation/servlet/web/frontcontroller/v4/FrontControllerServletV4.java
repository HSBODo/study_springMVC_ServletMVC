package foundation.servlet.web.frontcontroller.v4;


import foundation.servlet.web.frontcontroller.ModelView;
import foundation.servlet.web.frontcontroller.MyView;

import foundation.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import foundation.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import foundation.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//v4의 controller를 간소화 시켰다
//v5는 어댑텁 패턴을 사용해 핸들러 어댑터 추가하여 v1~4까지 유연하게 사용가능
@WebServlet(name = "frontControllerServletV4",urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {
    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerServletV4() { //생성자 컨트롤러 매핑 처리
        controllerMap.put("/front-controller/v4/members/new-form",new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save",new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members",new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        ControllerV4 controller = controllerMap.get(requestURI); //다형성 부모가 자식을 담을 수 있다.
        if(controller == null){ // 예외처리 컨트롤러 매핑정보가 없으면 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Map<String, String> paramMap = createParamMap(request);
        Map<String, Object> model = new HashMap<>(); //추가

        String viewName = controller.process(paramMap, model);

        MyView view = viewResolver(viewName);
        view.render(model,request,response);
    }

    private static MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private static Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
