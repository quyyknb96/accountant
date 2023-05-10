$(document).ready(function () {
    $('.remove-row').click(removeRowTable);

    function removeRowTable() {
        $(this).closest("tr").remove();
    }

    $(".add").click(function (){
        let tableName = $(this).data('table-name');
        let nameInput = $(this).data('name-input');
        let tagLastIndex = $(this).data('tag-last-index');
        addRowTable(tableName, nameInput, tagLastIndex);
    });

    function addRowTable(tableName , nameInput, tagLastIndex) {
        let index = $(tagLastIndex).val();
        let element = $(tableName).find("tbody tr:last");
        if (element.length === 0) {
            element= $(tableName).find("tbody:last");
        }
        element.after(
            " <tr>" +
            "   <td>\n" +
            "       <input autocomplete=\"off\" class=\"form-control\" name=\"" + nameInput + "[" + index + "].name\" />\n" +
            "   </td>\n" +
            "   <td>\n" +
            "       <input autocomplete=\"off\" class=\"form-control\" type=\"number\" name=\"" + nameInput + "[" + index + "].start\" />\n" +
            "    </td>\n" +
            "    <td>\n" +
            "       <input autocomplete=\"off\" class=\"form-control\" type=\"number\" name=\"" + nameInput + "[" + index + "].end\" />\n" +
            "    </td>\n" +
            "    <td><input autocomplete=\"off\" class=\"form-control remove-row btn btn-danger\" type=\"button\" value=\"Remove\" /></td>" +
            " </tr>"
        );
        $(tagLastIndex).val(parseInt(index) + 1);
        for (let i = 0 ; i < $(".remove-row").length; i++) {
            $(".remove-row")[i].addEventListener('click' , removeRowTable ) ;
        }
    }
});