function script_options
{
   while getopts :h:p: opt; do
      case ${opt} in
         h) HOST=$OPTARG
            ;;
         p) PORT=$OPTARG
            ;;
         \? )
            echo "Invalid Option: -$OPTARG" 1>&2
            exit 1
            ;;
         : )
            echo "Invalid Option: -$OPTARG requires an argument" 1>&2
            exit 1
            ;;
      esac
   done
   shift $((OPTIND -1))
}
