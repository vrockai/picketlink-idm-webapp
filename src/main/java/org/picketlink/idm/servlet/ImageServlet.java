/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.servlet.bean.UserBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;
import org.picketlink.idm.servlet.processor.IdmProcessorFactory;

/**
 *
 * @author vrockai
 */
public class ImageServlet extends IdmBasicServlet {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ImageServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("do image");
        try {
            IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();

            String userId = getStringParameter(request, "u", "");

            UserBean ub = idmProc.getUser(userId);
            
            if (ub.isPhoto()) {
                response.reset();
                response.setContentType("Image");
                response.getOutputStream().write(ub.getPhoto(), 0, ub.getPhoto().length);
                response.getOutputStream().flush();
            } else {
                response.sendRedirect("img/photo.jpg");
            }
        } catch (IdentityException ex) {
            log.info(ex);
        } finally {
            log.trace("end image");
        }

    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, java.io.IOException {
        log.trace("do post");
        response.setContentType("text/html;charset=UTF-8");

        log.trace("picture upload");

        PrintWriter out = response.getWriter();
        try {
            // Apache Commons-Fileupload library classes
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu = new ServletFileUpload(factory);

            if (!ServletFileUpload.isMultipartContent(request)) {
                log.info("sorry. No file uploaded");
                return;
            }

            // parse request
            List items = sfu.parseRequest(request);
            FileItem id = (FileItem) items.get(0);
            String userId = id.getString();

            log.info("User id for photo: " + userId);

            FileItem file = (FileItem) items.get(1);

            IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();
            idmProc.uploadPicture(userId, file.get());
            out.println("1");
        } catch (Exception ex) {
            log.info(ex);
        } finally {
            out.close();
            log.trace("end post");
        }

    }
}
